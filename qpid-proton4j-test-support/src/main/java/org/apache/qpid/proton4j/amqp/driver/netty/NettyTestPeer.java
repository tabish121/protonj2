/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton4j.amqp.driver.netty;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.qpid.proton4j.amqp.driver.AMQPTestDriver;
import org.apache.qpid.proton4j.amqp.driver.ScriptWriter;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty based Test Peer implementation.
 */
public class NettyTestPeer extends ScriptWriter implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(NettyTestPeer.class);

    private final ServerOptions options;
    private final AMQPTestDriver driver;
    private final TestDriverServer server;
    private final AtomicBoolean closed = new AtomicBoolean();
    private volatile Channel channel;

    /**
     * Creates a Socket Test Peer using all default Server options.
     */
    public NettyTestPeer() {
        this(new ServerOptions());
    }

    /**
     * Creates a Socket Test Peer using the options to configure the deployed server.
     *
     * @param options
     *      The options that control the behavior of the deployed server.
     */
    public NettyTestPeer(ServerOptions options) {
        this.options = options;
        this.driver = new AMQPTestDriver((frame) -> {
            processDriverOutput(frame);
        });
        this.server = new TestDriverServer(options);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            processCloseRequest();
        }
    }

    public boolean isClosed() {
        return closed.get();
    }

    //----- Channel handler that drives IO for the test driver

    private final class TestDriverServer extends NettyServer {

        public TestDriverServer(ServerOptions options) {
            super(options);
        }

        @Override
        protected ChannelHandler getServerHandler() {
            return new SimpleChannelInboundHandler<ByteBuf>() {

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf input) throws Exception {
                    LOG.trace("AMQP Server Channel read: {}", input);

                    try {
                        // Driver processes new data and may produce output based on this.
                        processChannelInput(new ProtonNettyByteBuffer(input));
                    } catch (Throwable e) {
                        ctx.channel().close();
                    } finally {
                    }
                }

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    channel = ctx.channel();
                    ctx.fireChannelActive();
                }
            };
        }
    }

    //----- Internal implementation which can be overridden

    protected void processCloseRequest() {
        if (channel != null) {
            try {
                if (!channel.close().await(10, TimeUnit.SECONDS)) {
                    LOG.info("Channel close timed out wiating for result");
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
                LOG.debug("Close of channel interrupted while awaiting result");
            }
        }
    }

    protected void processDriverOutput(ProtonBuffer frame) {
        LOG.trace("AMQP Server Channel writing: {}", frame);
        // TODO - Error handling for failed write
        // TODO - If we allow test driver to allocate io buffers we might save this copy
        //        if it can create an netty buffer wrapper.
        ByteBuf outbound = Unpooled.buffer(frame.getReadableBytes());
        outbound.writeBytes(frame.toByteBuffer());
        channel.writeAndFlush(outbound, channel.voidPromise());
    }

    protected void processChannelInput(ProtonBuffer input) {
        LOG.trace("AMQP Server Channel processing: {}", input);
        driver.accept(input);
    }

    @Override
    protected AMQPTestDriver getDriver() {
        return driver;
    }
}
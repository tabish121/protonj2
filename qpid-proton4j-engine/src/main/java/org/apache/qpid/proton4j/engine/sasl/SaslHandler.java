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
package org.apache.qpid.proton4j.engine.sasl;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.security.SaslPerformative;
import org.apache.qpid.proton4j.amqp.transport.AMQPHeader;
import org.apache.qpid.proton4j.amqp.transport.Performative;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.codec.CodecFactory;
import org.apache.qpid.proton4j.codec.Decoder;
import org.apache.qpid.proton4j.codec.Encoder;
import org.apache.qpid.proton4j.engine.EngineHandlerAdapter;
import org.apache.qpid.proton4j.engine.EngineHandlerContext;
import org.apache.qpid.proton4j.engine.HeaderFrame;
import org.apache.qpid.proton4j.engine.ProtocolFrame;
import org.apache.qpid.proton4j.engine.SaslFrame;
import org.apache.qpid.proton4j.engine.impl.ProtonFrameParser;

/**
 * Base class used for common portions of the SASL processing pipeline.
 */
public class SaslHandler extends EngineHandlerAdapter {

    private Decoder saslDecoder = CodecFactory.getSaslDecoder();
    private Encoder saslEncoder = CodecFactory.getSaslEncoder();

    private int maxFrameSizeLimit = SaslConstants.MAX_SASL_FRAME_SIZE;

    private ProtonFrameParser frameParser;
    private SaslContext saslContext;

    /*
     * The Handler must be create from the client or server methods to configure
     * the state correctly.
     */
    private SaslHandler() {
    }

    public Encoder getSaslEndoer() {
        return saslEncoder;
    }

    public void setSaslEncoder(Encoder encoder) {
        this.saslEncoder = encoder;
    }

    public Decoder getSaslDecoder() {
        return saslDecoder;
    }

    public void setSaslDecoder(Decoder decoder) {
        this.saslDecoder = decoder;
    }

    public void setMaxSaslFrameSize(int maxFrameSize) {
        this.maxFrameSizeLimit = Math.max(SaslConstants.MAX_SASL_FRAME_SIZE, maxFrameSize);
    }

    public int getMaxSaslFrameSize() {
        return maxFrameSizeLimit;
    }

    public boolean isDone() {
        return saslContext.isDone();
    }

    public static SaslHandler client(SaslClientListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("SaslClientListener must not be null");
        }

        SaslHandler handler = new SaslHandler();
        SaslClientContext context = new SaslClientContext(handler, listener);
        handler.saslContext = context;

        // Allow the application a change to configure the client handler
        listener.initialize(context);

        return handler;
    }

    public static SaslHandler server(SaslServerListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("SaslServerListener must not be null");
        }

        SaslHandler handler = new SaslHandler();
        SaslServerContext context = new SaslServerContext(handler, listener);
        handler.saslContext = context;

        // Allow the application a change to configure the server handler
        listener.initialize(context);

        return handler;
    }

    //----- TransportHandler implementation ----------------------------------//

    @Override
    public void handlerAdded(EngineHandlerContext context) throws Exception {
        frameParser = ProtonFrameParser.createSaslParser(this, saslDecoder, getMaxSaslFrameSize());
    }

    @Override
    public void handlerRemoved(EngineHandlerContext context) throws Exception {
        frameParser = null;
    }

    @Override
    public void handleRead(EngineHandlerContext context, ProtonBuffer buffer) {
        if (isDone()) {
            context.fireRead(buffer);
        } else {
            try {
                // Parse out each frame in the buffer until we reach an end state on SASL handling.
                // TODO - Need a state to indicate that sasl is now in a state where we can't process
                //        any more frames.  This would mean we are awaiting sasl outcome to move us to
                //        a done state, all pending data beyond this point should be protocol level frames.
                while (buffer.isReadable()) {
                    frameParser.parse(context, buffer);
                }
            } catch (IOException e) {
                // TODO - A more well defined exception API might allow for only
                //        one error event method ?
                context.fireDecodingError(e);
            }
        }
    }

    @Override
    public void handleRead(EngineHandlerContext context, HeaderFrame header) {
        if (isDone()) {
            context.fireRead(header);
        }

        saslContext.handleHeaderFrame(context, header);
    }

    @Override
    public void handleRead(EngineHandlerContext context, SaslFrame frame) {
        if (isDone()) {
            // TODO specific error for this case.
            context.fireFailed(new IllegalStateException(
                "Unexpected SASL Frame: SASL processing has already completed"));
        }

        frame.getBody().invoke(saslContext, context);
    }

    @Override
    public void handleRead(EngineHandlerContext context, ProtocolFrame frame) {
        if (isDone()) {
            context.fireRead(frame);
        } else {
            // TODO - We shouldn't be receiving these here if not done as we should be
            //        holding off on decoding the frames until after done and then passing
            //        them along to the next layer.

            // TODO specific error for this case.
            context.fireFailed(new IllegalStateException(
                "Unexpected AMQP Frame: SASL processing not yet completed"));
        }
    }

    // TODO - Decide what to implement and what to allow as a pass through

    @Override
    public void transportEncodingError(EngineHandlerContext context, Throwable e) {
    }

    @Override
    public void transportDecodingError(EngineHandlerContext context, Throwable e) {
    }

    @Override
    public void transportFailed(EngineHandlerContext context, Throwable e) {
    }

    @Override
    public void handleWrite(EngineHandlerContext context, AMQPHeader header) {
        if (isDone()) {
            // TODO We are done with sasl so this can be written to the transport as bytes
        } else {
            // TODO We are not done so this is not valid here and we should fail.
        }
    }

    @Override
    public void handleWrite(EngineHandlerContext context, Performative performative, short channel, ProtonBuffer payload, Runnable payloadToLarge) {
        if (isDone()) {
            // TODO We are done with sasl so this can be written to the transport as bytes
        } else {
            // TODO We are not done so this is not valid here and we should fail.
        }
    }

    @Override
    public void handleWrite(EngineHandlerContext context, SaslPerformative performative) {
        // TODO - Currently context routes it's writes here, but that presents some issues.
        //        Might be better if context does all the encoding and we decide on rules for
        //        what happens if user manually writes a sasl performative.

        if (isDone()) {
            // TODO We are done so writing a performative would be invalid.
        } else {
            // TODO We are not done but the context should process any external sasl performatives ?
            //      or is this always invalid and async sasl work should be done via the context ?
        }
    }

    @Override
    public void handleWrite(EngineHandlerContext context, ProtonBuffer buffer) {
        // TODO - in this case we don't know what is being written, if not done we should probably fail
        //        since we are controlling SASL here and if someone is trying to circumvent this handler
        //        that is not right.
    }
}
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
package org.apache.qpid.proton4j.engine.util;

import org.apache.qpid.proton4j.amqp.security.SaslPerformative;
import org.apache.qpid.proton4j.amqp.transport.AMQPHeader;
import org.apache.qpid.proton4j.amqp.transport.Performative;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.engine.EngineHandler;
import org.apache.qpid.proton4j.engine.EngineHandlerContext;
import org.apache.qpid.proton4j.engine.HeaderFrame;
import org.apache.qpid.proton4j.engine.ProtocolFrame;
import org.apache.qpid.proton4j.engine.SaslFrame;

/**
 * Drops all read frames in tests where no inbound frame handling is needed.
 */
public class FrameReadSinkTransportHandler implements EngineHandler {

    public FrameReadSinkTransportHandler() {
    }

    @Override
    public void handleRead(EngineHandlerContext context, HeaderFrame header) {
    }

    @Override
    public void handleRead(EngineHandlerContext context, SaslFrame frame) {
    }

    @Override
    public void handleRead(EngineHandlerContext context, ProtocolFrame frame) {
    }

    @Override
    public void handleWrite(EngineHandlerContext context, AMQPHeader header) {
        context.fireWrite(header);
    }

    @Override
    public void handleWrite(EngineHandlerContext context, Performative performative, int channel, ProtonBuffer payload, Runnable payloadToLarge) {
        context.fireWrite(performative, channel, payload, payloadToLarge);
    }

    @Override
    public void handleWrite(EngineHandlerContext context, SaslPerformative performative) {
        context.fireWrite(performative);
    }
}
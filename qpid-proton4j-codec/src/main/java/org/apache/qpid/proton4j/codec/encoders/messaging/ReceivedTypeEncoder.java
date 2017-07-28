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
package org.apache.qpid.proton4j.codec.encoders.messaging;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.messaging.Received;
import org.apache.qpid.proton4j.codec.DescribedListTypeEncoder;
import org.apache.qpid.proton4j.codec.EncoderState;
import org.apache.qpid.proton4j.codec.EncodingCodes;

import io.netty.buffer.ByteBuf;

/**
 * Encoder of AMQP Received type values from a byte stream.
 */
public class ReceivedTypeEncoder implements DescribedListTypeEncoder<Received> {

    @Override
    public UnsignedLong getDescriptorCode() {
        return Received.DESCRIPTOR_CODE;
    }

    @Override
    public Symbol getDescriptorSymbol() {
        return Received.DESCRIPTOR_SYMBOL;
    }

    @Override
    public Class<Received> getTypeClass() {
        return Received.class;
    }

    @Override
    public int getLargestEncoding() {
        return EncodingCodes.LIST8 & 0xff;
    }

    @Override
    public void writeElement(Received source, int index, ByteBuf buffer, EncoderState state) {
        switch (index) {
            case 0:
                state.getEncoder().writeUnsignedInteger(buffer, state, source.getSectionNumber());
                break;
            case 1:
                state.getEncoder().writeUnsignedLong(buffer, state, source.getSectionOffset());
                break;
            default:
                throw new IllegalArgumentException("Unknown Received value index: " + index);
        }
    }

    @Override
    public int getElementCount(Received value) {
        if (value.getSectionOffset() != null) {
            return 2;
        } else if (value.getSectionNumber() != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
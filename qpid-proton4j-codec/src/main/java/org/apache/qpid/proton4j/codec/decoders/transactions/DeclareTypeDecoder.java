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
package org.apache.qpid.proton4j.codec.decoders.transactions;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.transactions.Declare;
import org.apache.qpid.proton4j.amqp.transactions.GlobalTxId;
import org.apache.qpid.proton4j.codec.DecoderState;
import org.apache.qpid.proton4j.codec.DescribedTypeDecoder;
import org.apache.qpid.proton4j.codec.TypeDecoder;
import org.apache.qpid.proton4j.codec.decoders.primitives.ListTypeDecoder;
import org.apache.qpid.proton4j.codec.decoders.primitives.ListTypeDecoder.ListEntryHandler;

import io.netty.buffer.ByteBuf;

/**
 * Decoder of AMQP Declare type values from a byte stream
 */
public class DeclareTypeDecoder implements DescribedTypeDecoder<Declare>, ListEntryHandler<Declare> {

    @Override
    public Class<Declare> getTypeClass() {
        return Declare.class;
    }

    @Override
    public UnsignedLong getDescriptorCode() {
        return Declare.DESCRIPTOR_CODE;
    }

    @Override
    public Symbol getDescriptorSymbol() {
        return Declare.DESCRIPTOR_SYMBOL;
    }

    @Override
    public Declare readValue(ByteBuf buffer, DecoderState state) throws IOException {

        TypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(buffer, state);

        if (!(decoder instanceof ListTypeDecoder)) {
            throw new IOException("Expected List type indicator but got decoder for type: " + decoder.getTypeClass().getName());
        }

        ListTypeDecoder listDecoder = (ListTypeDecoder) decoder;
        Declare declare = new Declare();

        listDecoder.readValue(buffer, state, this, declare);

        return declare;
    }

    @Override
    public void onListEntry(int index, Declare declare, ByteBuf buffer, DecoderState state) throws IOException {
        switch (index) {
            case 0:
                declare.setGlobalId(state.getDecoder().readObject(buffer, state, GlobalTxId.class));
                break;
            default:
                throw new IllegalStateException("To many entries in Declare encoding");
        }
    }
}
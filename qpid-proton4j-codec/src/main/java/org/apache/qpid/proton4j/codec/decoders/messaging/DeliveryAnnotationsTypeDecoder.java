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
package org.apache.qpid.proton4j.codec.decoders.messaging;

import java.io.IOException;
import java.util.Map;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.messaging.DeliveryAnnotations;
import org.apache.qpid.proton4j.codec.DecoderState;
import org.apache.qpid.proton4j.codec.DescribedTypeDecoder;
import org.apache.qpid.proton4j.codec.TypeDecoder;
import org.apache.qpid.proton4j.codec.decoders.primitives.MapTypeDecoder;

import io.netty.buffer.ByteBuf;

/**
 * Decoder of AMQP Delivery Annotations type values from a byte stream.
 */
public class DeliveryAnnotationsTypeDecoder implements DescribedTypeDecoder<DeliveryAnnotations> {

    @Override
    public Class<DeliveryAnnotations> getTypeClass() {
        return DeliveryAnnotations.class;
    }

    @Override
    public UnsignedLong getDescriptorCode() {
        return DeliveryAnnotations.DESCRIPTOR_CODE;
    }

    @Override
    public Symbol getDescriptorSymbol() {
        return DeliveryAnnotations.DESCRIPTOR_SYMBOL;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeliveryAnnotations readValue(ByteBuf buffer, DecoderState state) throws IOException {
        TypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(buffer, state);

        if (!(decoder instanceof MapTypeDecoder)) {
            throw new IOException("Expected Map type indicator but got decoder for type: " + decoder.getClass().getSimpleName());
        }

        MapTypeDecoder mapDecoder = (MapTypeDecoder) decoder;

        Map map = mapDecoder.readValue(buffer, state);
        return new DeliveryAnnotations(map);
    }
}
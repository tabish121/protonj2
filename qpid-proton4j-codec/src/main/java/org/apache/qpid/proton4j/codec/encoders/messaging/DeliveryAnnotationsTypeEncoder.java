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

import java.util.Map.Entry;
import java.util.Set;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.messaging.DeliveryAnnotations;
import org.apache.qpid.proton4j.codec.DescribedMapTypeEncoder;

/**
 * Encoder of AMQP DeliveryAnnotations type values to a byte stream.
 */
public class DeliveryAnnotationsTypeEncoder implements DescribedMapTypeEncoder<Symbol, Object, DeliveryAnnotations> {

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

    @Override
    public boolean hasMap(DeliveryAnnotations value) {
        return value.getValue() != null;
    }

    @Override
    public int getMapSize(DeliveryAnnotations value) {
        if (value.getValue() != null) {
            return value.getValue().size();
        } else {
            return 0;
        }
    }

    @Override
    public Set<Entry<Symbol, Object>> getMapEntries(DeliveryAnnotations value) {
        if (value.getValue() != null) {
            return value.getValue().entrySet();
        } else {
            return null;
        }
    }
}
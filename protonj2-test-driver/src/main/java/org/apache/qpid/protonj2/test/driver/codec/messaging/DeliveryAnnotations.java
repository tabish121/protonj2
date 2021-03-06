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
package org.apache.qpid.protonj2.test.driver.codec.messaging;

import org.apache.qpid.protonj2.test.driver.codec.MapDescribedType;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedLong;

public class DeliveryAnnotations extends MapDescribedType {

    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000071L);
    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:delivery-annotations:map");

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public void setSymbolKeyedAnnotation(String name, Object value) {
        getDescribed().put(Symbol.valueOf(name), value);
    }

    public void setSymbolKeyedAnnotation(Symbol name, Object value) {
        getDescribed().put(name, value);
    }

    public void setUnsignedLongKeyedAnnotation(UnsignedLong name, Object value) {
        throw new UnsupportedOperationException("UnsignedLong keys are currently reserved");
    }
}

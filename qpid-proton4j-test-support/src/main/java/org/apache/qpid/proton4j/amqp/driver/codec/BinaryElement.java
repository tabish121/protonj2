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
package org.apache.qpid.proton4j.amqp.driver.codec;

import java.nio.ByteBuffer;

import org.apache.qpid.proton4j.amqp.Binary;

class BinaryElement extends AtomicElement<Binary> {

    private final Binary value;

    BinaryElement(Element<?> parent, Element<?> prev, Binary b) {
        super(parent, prev);
        byte[] data = new byte[b.getLength()];
        System.arraycopy(b.getArray(), b.getArrayOffset(), data, 0, b.getLength());
        value = new Binary(data);
    }

    @Override
    public int size() {
        final int length = value.getLength();

        if (isElementOfArray()) {
            final ArrayElement parent = (ArrayElement) parent();

            if (parent.constructorType() == ArrayElement.SMALL) {
                if (length > 255) {
                    parent.setConstructorType(ArrayElement.LARGE);
                    return 4 + length;
                } else {
                    return 1 + length;
                }
            } else {
                return 4 + length;
            }
        } else {
            if (length > 255) {
                return 5 + length;
            } else {
                return 2 + length;
            }
        }
    }

    @Override
    public Binary getValue() {
        return value;
    }

    @Override
    public Data.DataType getDataType() {
        return Data.DataType.BINARY;
    }

    @Override
    public int encode(ByteBuffer b) {
        int size = size();
        if (b.remaining() < size) {
            return 0;
        }
        if (isElementOfArray()) {
            final ArrayElement parent = (ArrayElement) parent();

            if (parent.constructorType() == ArrayElement.SMALL) {
                b.put((byte) value.getLength());
            } else {
                b.putInt(value.getLength());
            }
        } else if (value.getLength() <= 255) {
            b.put((byte) 0xa0);
            b.put((byte) value.getLength());
        } else {
            b.put((byte) 0xb0);
            b.putInt(value.getLength());
        }
        b.put(value.getArray(), value.getArrayOffset(), value.getLength());
        return size;
    }
}
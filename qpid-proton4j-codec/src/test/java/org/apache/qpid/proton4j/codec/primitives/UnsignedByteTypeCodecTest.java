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
package org.apache.qpid.proton4j.codec.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.UnsignedByte;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.codec.CodecTestSupport;
import org.junit.Test;

public class UnsignedByteTypeCodecTest extends CodecTestSupport {

    @Test
    public void testEncodeDecodeUnsignedByte() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        encoder.writeUnsignedByte(buffer, encoderState, UnsignedByte.valueOf((byte) 64));

        Object result = decoder.readObject(buffer, decoderState);
        assertTrue(result instanceof UnsignedByte);
        assertEquals(64, ((UnsignedByte) result).byteValue());
    }

    @Test
    public void testEncodeDecodeByte() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        encoder.writeUnsignedByte(buffer, encoderState, (byte) 64);

        Object result = decoder.readObject(buffer, decoderState);
        assertTrue(result instanceof UnsignedByte);
        assertEquals(64, ((UnsignedByte) result).byteValue());
    }

    @Test
    public void testDecodeSmallSeriesOfUnsignedBytes() throws IOException {
        doTestDecodeUnsignedByteSeries(SMALL_SIZE);
    }

    @Test
    public void testDecodeLargeSeriesOfUnsignedBytes() throws IOException {
        doTestDecodeUnsignedByteSeries(LARGE_SIZE);
    }

    private void doTestDecodeUnsignedByteSeries(int size) throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        for (int i = 0; i < size; ++i) {
            encoder.writeUnsignedByte(buffer, encoderState, (byte)(i % 255));
        }

        for (int i = 0; i < size; ++i) {
            final UnsignedByte result = decoder.readUnsignedByte(buffer, decoderState);

            assertNotNull(result);
            assertEquals((byte)(i % 255), result.byteValue());
        }
    }

    @Test
    public void testArrayOfUnsignedByteObjects() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        final int size = 10;

        UnsignedByte[] source = new UnsignedByte[size];
        for (int i = 0; i < size; ++i) {
            source[i] = UnsignedByte.valueOf((byte) (i % 255));
        }

        encoder.writeArray(buffer, encoderState, source);

        Object result = decoder.readObject(buffer, decoderState);
        assertNotNull(result);
        assertTrue(result.getClass().isArray());
        assertFalse(result.getClass().getComponentType().isPrimitive());

        UnsignedByte[] array = (UnsignedByte[]) result;
        assertEquals(size, array.length);

        for (int i = 0; i < size; ++i) {
            assertEquals(source[i], array[i]);
        }
    }

    @Test
    public void testZeroSizedArrayOfUnsignedByteObjects() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        UnsignedByte[] source = new UnsignedByte[0];

        encoder.writeArray(buffer, encoderState, source);

        Object result = decoder.readObject(buffer, decoderState);
        assertNotNull(result);
        assertTrue(result.getClass().isArray());
        assertFalse(result.getClass().getComponentType().isPrimitive());

        UnsignedByte[] array = (UnsignedByte[]) result;
        assertEquals(source.length, array.length);
    }
}
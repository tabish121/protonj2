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
package org.apache.qpid.proton4j.codec.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.qpid.proton4j.amqp.messaging.AmqpSequence;
import org.apache.qpid.proton4j.amqp.messaging.Modified;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.codec.CodecTestSupport;
import org.apache.qpid.proton4j.codec.TypeDecoder;
import org.apache.qpid.proton4j.codec.decoders.messaging.AmqpSequenceTypeDecoder;
import org.apache.qpid.proton4j.codec.encoders.messaging.AmqpSequenceTypeEncoder;
import org.junit.Test;

/**
 * Test for decoder of the AmqpValue type.
 */
public class AmqpSequenceTypeCodecTest extends CodecTestSupport {

    @Test
    public void testTypeClassReturnsCorrectType() throws IOException {
        assertEquals(AmqpSequence.class, new AmqpSequenceTypeDecoder().getTypeClass());
        assertEquals(AmqpSequence.class, new AmqpSequenceTypeEncoder().getTypeClass());
    }

    @Test
    public void testDecodeAmqpValueString() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<Object> list = new ArrayList<>();

        list.add(UUID.randomUUID());
        list.add("string");

        AmqpSequence value = new AmqpSequence(list);

        encoder.writeObject(buffer, encoderState, value);

        final Object result = decoder.readObject(buffer, decoderState);

        assertNotNull(result);
        assertTrue(result instanceof AmqpSequence);

        AmqpSequence decoded = (AmqpSequence) result;

        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testEncodeDecodeArrayOfAmqpSequence() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<Object> list = new ArrayList<>();

        list.add("test-1");
        list.add("test-2");

        AmqpSequence[] array = new AmqpSequence[3];

        array[0] = new AmqpSequence(list);
        array[1] = new AmqpSequence(list);
        array[2] = new AmqpSequence(list);

        encoder.writeObject(buffer, encoderState, array);

        final Object result = decoder.readObject(buffer, decoderState);

        assertTrue(result.getClass().isArray());
        assertEquals(AmqpSequence.class, result.getClass().getComponentType());

        AmqpSequence[] resultArray = (AmqpSequence[]) result;

        for (int i = 0; i < resultArray.length; ++i) {
            assertNotNull(resultArray[i]);
            assertTrue(resultArray[i] instanceof AmqpSequence);
            assertEquals(array[i].getValue(), resultArray[i].getValue());
        }
    }

    @Test
    public void testSkipValue() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<Object> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        for (int i = 0; i < 10; ++i) {
            encoder.writeObject(buffer, encoderState, new AmqpSequence(list));
        }

        encoder.writeObject(buffer, encoderState, new Modified());

        for (int i = 0; i < 10; ++i) {
            TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
            assertEquals(AmqpSequence.class, typeDecoder.getTypeClass());
            typeDecoder.skipValue(buffer, decoderState);
        }

        final Object result = decoder.readObject(buffer, decoderState);

        assertNotNull(result);
        assertTrue(result instanceof Modified);
        Modified modified = (Modified) result;
        assertFalse(modified.getUndeliverableHere());
        assertFalse(modified.getDeliveryFailed());
    }
}

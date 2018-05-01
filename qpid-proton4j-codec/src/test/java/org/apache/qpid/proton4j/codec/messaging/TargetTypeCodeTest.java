/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton4j.codec.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.amqp.messaging.Target;
import org.apache.qpid.proton4j.amqp.messaging.TerminusDurability;
import org.apache.qpid.proton4j.amqp.messaging.TerminusExpiryPolicy;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.codec.CodecTestSupport;
import org.junit.Test;

/**
 * Test for handling Source serialization
 */
public class TargetTypeCodeTest extends CodecTestSupport {

   @Test
   public void testEncodeDecodeOfTarget() throws Exception {
      ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

      Target value = new Target();
      value.setAddress("test");
      value.setDurable(TerminusDurability.UNSETTLED_STATE);

      encoder.writeObject(buffer, encoderState, value);

      final Target result = (Target)decoder.readObject(buffer, decoderState);

      assertEquals("test", result.getAddress());
      assertEquals(TerminusDurability.UNSETTLED_STATE, result.getDurable());
   }

   @Test
   public void testFullyPopulatedTarget() throws Exception {
      ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

      Map<Symbol, Object> nodeProperties = new LinkedHashMap<>();
      nodeProperties.put(Symbol.valueOf("property-1"), "value-1");
      nodeProperties.put(Symbol.valueOf("property-2"), "value-2");
      nodeProperties.put(Symbol.valueOf("property-3"), "value-3");

      Target value = new Target();
      value.setAddress("test");
      value.setDurable(TerminusDurability.UNSETTLED_STATE);
      value.setExpiryPolicy(TerminusExpiryPolicy.CONNECTION_CLOSE);
      value.setTimeout(UnsignedInteger.valueOf(1024));
      value.setDynamic(false);
      value.setCapabilities(new Symbol[] {Symbol.valueOf("RELEASED"), Symbol.valueOf("MODIFIED")});
      value.setDynamicNodeProperties(nodeProperties);

      encoder.writeObject(buffer, encoderState, value);

      final Target result = (Target)decoder.readObject(buffer, decoderState);

      assertEquals("test", result.getAddress());
      assertEquals(TerminusDurability.UNSETTLED_STATE, result.getDurable());
      assertEquals(TerminusExpiryPolicy.CONNECTION_CLOSE, result.getExpiryPolicy());
      assertEquals(UnsignedInteger.valueOf(1024), result.getTimeout());
      assertEquals(false, result.getDynamic());
      assertEquals(nodeProperties, result.getDynamicNodeProperties());
      assertArrayEquals(new Symbol[] {Symbol.valueOf("RELEASED"), Symbol.valueOf("MODIFIED")}, result.getCapabilities());
   }
}
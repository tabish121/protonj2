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
package org.apache.qpid.proton4j.codec.util;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.codec.DescribedTypeEncoder;
import org.apache.qpid.proton4j.codec.EncoderState;

public class NoLocalTypeEncoder implements DescribedTypeEncoder<NoLocalType> {

    @Override
    public Class<NoLocalType> getTypeClass() {
        return NoLocalType.class;
    }

    @Override
    public UnsignedLong getDescriptorCode() {
        return NoLocalType.DESCRIPTOR_CODE;
    }

    @Override
    public Symbol getDescriptorSymbol() {
        return NoLocalType.DESCRIPTOR_SYMBOL;
    }

    @Override
    public void writeValue(ProtonBuffer buffer, EncoderState state, NoLocalType value) {
        state.getEncoder().writeString(buffer, state, value.getDescribed());
    }
}
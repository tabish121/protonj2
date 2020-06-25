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
package org.apache.qpid.protonj2.types.messaging;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.qpid.protonj2.types.Symbol;
import org.apache.qpid.protonj2.types.UnsignedLong;

public final class ApplicationProperties implements Section {

    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000074L);
    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:application-properties:map");

    private final Map<String, Object> value;

    @SuppressWarnings("unchecked")
    public ApplicationProperties(Map<String, ?> value) {
        this.value = (Map<String, Object>) value;
    }

    public ApplicationProperties copy() {
        return new ApplicationProperties(value == null ? null : new LinkedHashMap<>(value));
    }

    public Map<String, Object> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{ " + value + " }";
    }

    @Override
    public SectionType getType() {
        return SectionType.ApplicationProperties;
    }
}
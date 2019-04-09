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
package org.apache.qpid.proton4j.amqp.driver.codec.types;

import java.util.List;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.driver.codec.ListDescribedType;

public class Source extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:source:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000028L);

    /**
     * Enumeration which maps to fields in the Source Performative
     */
    public enum Field {
        ADDRESS,
        DURABLE,
        EXPIRY_POLICY,
        TIMEOUT,
        DYNAMIC,
        DYNAMIC_NODE_PROPERTIES,
        DISTRIBUTION_MODE,
        FILTER,
        DEFAULT_OUTCOME,
        OUTCOMES,
        CAPABILITIES,
    }

    public Source() {
        super(Field.values().length);
    }

    @SuppressWarnings("unchecked")
    public Source(Object described) {
        super(Field.values().length, (List<Object>) described);
    }

    public Source(List<Object> described) {
        super(Field.values().length, described);
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public Source setAddress(Object o) {
        getList().set(Field.ADDRESS.ordinal(), o);
        return this;
    }

    public Object getAddress() {
        return getList().get(Field.ADDRESS.ordinal());
    }

    public Source setDurable(Object o) {
        getList().set(Field.DURABLE.ordinal(), o);
        return this;
    }

    public Object getDurable() {
        return getList().get(Field.DURABLE.ordinal());
    }

    public Source setExpiryPolicy(Object o) {
        getList().set(Field.EXPIRY_POLICY.ordinal(), o);
        return this;
    }

    public Object getExpiryPolicy() {
        return getList().get(Field.EXPIRY_POLICY.ordinal());
    }

    public Source setTimeout(Object o) {
        getList().set(Field.TIMEOUT.ordinal(), o);
        return this;
    }

    public Object getTimeout() {
        return getList().get(Field.TIMEOUT.ordinal());
    }

    public Source setDynamic(Object o) {
        getList().set(Field.DYNAMIC.ordinal(), o);
        return this;
    }

    public Object getDynamic() {
        return getList().get(Field.DYNAMIC.ordinal());
    }

    public Source setDynamicNodeProperties(Object o) {
        getList().set(Field.DYNAMIC_NODE_PROPERTIES.ordinal(), o);
        return this;
    }

    public Object getDynamicNodeProperties() {
        return getList().get(Field.DYNAMIC_NODE_PROPERTIES.ordinal());
    }

    public Source setDistributionMode(Object o) {
        getList().set(Field.DISTRIBUTION_MODE.ordinal(), o);
        return this;
    }

    public Object getDistributionMode() {
        return getList().get(Field.DISTRIBUTION_MODE.ordinal());
    }

    public Source setFilter(Object o) {
        getList().set(Field.FILTER.ordinal(), o);
        return this;
    }

    public Object getFilter() {
        return getList().get(Field.FILTER.ordinal());
    }

    public Source setDefaultOutcome(Object o) {
        getList().set(Field.DEFAULT_OUTCOME.ordinal(), o);
        return this;
    }

    public Object getDefaultOutcome() {
        return getList().get(Field.DEFAULT_OUTCOME.ordinal());
    }

    public Source setOutcomes(Object o) {
        getList().set(Field.OUTCOMES.ordinal(), o);
        return this;
    }

    public Object getOutcomes() {
        return getList().get(Field.OUTCOMES.ordinal());
    }

    public Source setCapabilities(Object o) {
        getList().set(Field.CAPABILITIES.ordinal(), o);
        return this;
    }

    public Object getCapabilities() {
        return getList().get(Field.CAPABILITIES.ordinal());
    }
}

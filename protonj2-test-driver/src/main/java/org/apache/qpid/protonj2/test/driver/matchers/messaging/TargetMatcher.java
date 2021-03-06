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
package org.apache.qpid.protonj2.test.driver.matchers.messaging;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Map;

import org.apache.qpid.protonj2.test.driver.codec.messaging.Target;
import org.apache.qpid.protonj2.test.driver.codec.messaging.TerminusDurability;
import org.apache.qpid.protonj2.test.driver.codec.messaging.TerminusExpiryPolicy;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedInteger;
import org.apache.qpid.protonj2.test.driver.codec.util.TypeMapper;
import org.apache.qpid.protonj2.test.driver.matchers.ListDescribedTypeMatcher;
import org.hamcrest.Matcher;

public class TargetMatcher extends ListDescribedTypeMatcher {

    public TargetMatcher() {
        super(Target.Field.values().length, Target.DESCRIPTOR_CODE, Target.DESCRIPTOR_SYMBOL);
    }

    public TargetMatcher(Target target) {
        super(Target.Field.values().length, Target.DESCRIPTOR_CODE, Target.DESCRIPTOR_SYMBOL);

        addTargetMatchers(target);
    }

    @Override
    protected Class<?> getDescribedTypeClass() {
        return Target.class;
    }

    //----- Type specific with methods that perform simple equals checks

    public TargetMatcher withAddress(String name) {
        return withAddress(equalTo(name));
    }

    public TargetMatcher withDurable(TerminusDurability durability) {
        return withDurable(equalTo(durability.getValue()));
    }

    public TargetMatcher withExpiryPolicy(TerminusExpiryPolicy expiry) {
        return withExpiryPolicy(equalTo(expiry.getPolicy()));
    }

    public TargetMatcher withTimeout(int timeout) {
        return withTimeout(equalTo(UnsignedInteger.valueOf(timeout)));
    }

    public TargetMatcher withTimeout(long timeout) {
        return withTimeout(equalTo(UnsignedInteger.valueOf(timeout)));
    }

    public TargetMatcher withTimeout(UnsignedInteger timeout) {
        return withTimeout(equalTo(timeout));
    }

    public TargetMatcher withDefaultTimeout() {
        return withTimeout(anyOf(nullValue(), equalTo(UnsignedInteger.ZERO)));
    }

    public TargetMatcher withDynamic(boolean dynamic) {
        return withDynamic(equalTo(dynamic));
    }

    public TargetMatcher withDynamicNodeProperties(Map<String, Object> properties) {
        return withDynamicNodeProperties(equalTo(TypeMapper.toSymbolKeyedMap(properties)));
    }

    public TargetMatcher withDynamicNodePropertiesMap(Map<Symbol, Object> properties) {
        return withDynamicNodeProperties(equalTo(properties));
    }

    public TargetMatcher withCapabilities(String... capabilities) {
        return withCapabilities(equalTo(TypeMapper.toSymbolArray(capabilities)));
    }

    public TargetMatcher withCapabilities(Symbol... capabilities) {
        return withCapabilities(equalTo(capabilities));
    }

    //----- Matcher based with methods for more complex validation

    public TargetMatcher withAddress(Matcher<?> m) {
        addFieldMatcher(Target.Field.ADDRESS, m);
        return this;
    }

    public TargetMatcher withDurable(Matcher<?> m) {
        addFieldMatcher(Target.Field.DURABLE, m);
        return this;
    }

    public TargetMatcher withExpiryPolicy(Matcher<?> m) {
        addFieldMatcher(Target.Field.EXPIRY_POLICY, m);
        return this;
    }

    public TargetMatcher withTimeout(Matcher<?> m) {
        addFieldMatcher(Target.Field.TIMEOUT, m);
        return this;
    }

    public TargetMatcher withDynamic(Matcher<?> m) {
        addFieldMatcher(Target.Field.DYNAMIC, m);
        return this;
    }

    public TargetMatcher withDynamicNodeProperties(Matcher<?> m) {
        addFieldMatcher(Target.Field.DYNAMIC_NODE_PROPERTIES, m);
        return this;
    }

    public TargetMatcher withCapabilities(Matcher<?> m) {
        addFieldMatcher(Target.Field.CAPABILITIES, m);
        return this;
    }

    //----- Populate the matcher from a given Source object

    private void addTargetMatchers(Target target) {
        if (target.getAddress() != null) {
            addFieldMatcher(Target.Field.ADDRESS, equalTo(target.getAddress()));
        } else {
            addFieldMatcher(Target.Field.ADDRESS, nullValue());
        }

        if (target.getDurable() != null) {
            addFieldMatcher(Target.Field.DURABLE, equalTo(target.getDurable()));
        } else {
            addFieldMatcher(Target.Field.DURABLE, nullValue());
        }

        if (target.getExpiryPolicy() != null) {
            addFieldMatcher(Target.Field.EXPIRY_POLICY, equalTo(target.getExpiryPolicy()));
        } else {
            addFieldMatcher(Target.Field.EXPIRY_POLICY, nullValue());
        }

        if (target.getTimeout() != null) {
            addFieldMatcher(Target.Field.TIMEOUT, equalTo(target.getTimeout()));
        } else {
            addFieldMatcher(Target.Field.TIMEOUT, nullValue());
        }

        addFieldMatcher(Target.Field.DYNAMIC, equalTo(target.getDynamic()));

        if (target.getDynamicNodeProperties() != null) {
            addFieldMatcher(Target.Field.DYNAMIC_NODE_PROPERTIES, equalTo(target.getDynamicNodeProperties()));
        } else {
            addFieldMatcher(Target.Field.DYNAMIC_NODE_PROPERTIES, nullValue());
        }

        if (target.getCapabilities() != null) {
            addFieldMatcher(Target.Field.CAPABILITIES, equalTo(target.getCapabilities()));
        } else {
            addFieldMatcher(Target.Field.CAPABILITIES, nullValue());
        }
    }
}

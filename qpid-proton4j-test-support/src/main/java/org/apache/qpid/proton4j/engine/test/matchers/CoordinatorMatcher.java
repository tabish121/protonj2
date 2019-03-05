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
package org.apache.qpid.proton4j.engine.test.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.util.List;

import org.apache.qpid.proton4j.amqp.DescribedType;
import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.engine.test.AbstractFieldAndDescriptorMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CoordinatorMatcher extends TypeSafeMatcher<Object> {

    private CoordinatorMatcherCore coreMatcher = new CoordinatorMatcherCore();
    private String mismatchTextAddition;
    private Object described;
    private Object descriptor;

    public CoordinatorMatcher() {
    }

    @Override
    protected boolean matchesSafely(Object received) {
        try {
            assertThat(received, instanceOf(DescribedType.class));
            descriptor = ((DescribedType) received).getDescriptor();
            if (!coreMatcher.descriptorMatches(descriptor)) {
                mismatchTextAddition = "Descriptor mismatch";
                return false;
            }

            described = ((DescribedType) received).getDescribed();
            assertThat(described, instanceOf(List.class));
            @SuppressWarnings("unchecked")
            List<Object> fields = (List<Object>) described;

            coreMatcher.verifyFields(fields);
        } catch (AssertionError ae) {
            mismatchTextAddition = "AssertionFailure: " + ae.getMessage();
            return false;
        }

        return true;
    }

    @Override
    protected void describeMismatchSafely(Object item, Description mismatchDescription) {
        mismatchDescription.appendText("\nActual form: ").appendValue(item);

        mismatchDescription.appendText("\nExpected descriptor: ").appendValue(coreMatcher.getSymbolicDescriptor()).appendText(" / ")
            .appendValue(coreMatcher.getNumericDescriptor());

        if (mismatchTextAddition != null) {
            mismatchDescription.appendText("\nAdditional info: ").appendValue(mismatchTextAddition);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Coordinator which matches: ").appendValue(coreMatcher.getMatchers());
    }

    public CoordinatorMatcher withCapabilities(Matcher<?> m) {
        coreMatcher.withCapabilities(m);
        return this;
    }

    public Object getReceivedCapabilities() {
        return coreMatcher.getReceivedCapabilities();
    }

    // Inner core matching class
    public static class CoordinatorMatcherCore extends AbstractFieldAndDescriptorMatcher {
        /**
         * Note that the ordinals of the Field enums match the order specified
         * in the AMQP spec
         */
        public enum Field {
            CAPABILITIES,
        }

        public CoordinatorMatcherCore() {
            super(UnsignedLong.valueOf(0x0000000000000030L), Symbol.valueOf("amqp:coordinator:list"));
        }

        public CoordinatorMatcherCore withCapabilities(Matcher<?> m) {
            getMatchers().put(Field.CAPABILITIES, m);
            return this;
        }

        public Object getReceivedCapabilities() {
            return getReceivedFields().get(Field.CAPABILITIES);
        }

        @Override
        protected Enum<?> getField(int fieldIndex) {
            return Field.values()[fieldIndex];
        }
    }
}
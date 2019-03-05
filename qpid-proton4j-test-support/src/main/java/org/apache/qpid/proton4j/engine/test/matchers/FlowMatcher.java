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

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.engine.test.FrameType;
import org.apache.qpid.proton4j.engine.test.FrameWithNoPayloadMatchingHandler;
import org.hamcrest.Matcher;

public class FlowMatcher extends FrameWithNoPayloadMatchingHandler {

    /**
     * Note that the ordinals of the Field enums match the order specified in
     * the AMQP spec
     */
    public enum Field {
        NEXT_INCOMING_ID, INCOMING_WINDOW, NEXT_OUTGOING_ID, OUTGOING_WINDOW, HANDLE, DELIVERY_COUNT, LINK_CREDIT, AVAILABLE, DRAIN, ECHO, PROPERTIES,
    }

    public FlowMatcher() {
        super(FrameType.AMQP, ANY_CHANNEL, UnsignedLong.valueOf(0x0000000000000013L), Symbol.valueOf("amqp:flow:list"));
    }

    @Override
    public FlowMatcher onCompletion(Runnable onCompletion) {
        super.onCompletion(onCompletion);
        return this;
    }

    public FlowMatcher withNextIncomingId(Matcher<?> m) {
        getMatchers().put(Field.NEXT_INCOMING_ID, m);
        return this;
    }

    public FlowMatcher withIncomingWindow(Matcher<?> m) {
        getMatchers().put(Field.INCOMING_WINDOW, m);
        return this;
    }

    public FlowMatcher withNextOutgoingId(Matcher<?> m) {
        getMatchers().put(Field.NEXT_OUTGOING_ID, m);
        return this;
    }

    public FlowMatcher withOutgoingWindow(Matcher<?> m) {
        getMatchers().put(Field.OUTGOING_WINDOW, m);
        return this;
    }

    public FlowMatcher withHandle(Matcher<?> m) {
        getMatchers().put(Field.HANDLE, m);
        return this;
    }

    public FlowMatcher withDeliveryCount(Matcher<?> m) {
        getMatchers().put(Field.DELIVERY_COUNT, m);
        return this;
    }

    public FlowMatcher withLinkCredit(Matcher<?> m) {
        getMatchers().put(Field.LINK_CREDIT, m);
        return this;
    }

    public FlowMatcher withAvailable(Matcher<?> m) {
        getMatchers().put(Field.AVAILABLE, m);
        return this;
    }

    public FlowMatcher withDrain(Matcher<?> m) {
        getMatchers().put(Field.DRAIN, m);
        return this;
    }

    public FlowMatcher withEcho(Matcher<?> m) {
        getMatchers().put(Field.ECHO, m);
        return this;
    }

    public FlowMatcher withProperties(Matcher<?> m) {
        getMatchers().put(Field.PROPERTIES, m);
        return this;
    }

    public Object getReceivedNextIncomingId() {
        return getReceivedFields().get(Field.NEXT_INCOMING_ID);
    }

    public Object getReceivedIncomingWindow() {
        return getReceivedFields().get(Field.INCOMING_WINDOW);
    }

    public Object getReceivedNextOutgoingId() {
        return getReceivedFields().get(Field.NEXT_OUTGOING_ID);
    }

    public Object getReceivedOutgoingWindow() {
        return getReceivedFields().get(Field.OUTGOING_WINDOW);
    }

    public Object getReceivedHandle() {
        return getReceivedFields().get(Field.HANDLE);
    }

    public Object getReceivedDeliveryCount() {
        return getReceivedFields().get(Field.DELIVERY_COUNT);
    }

    public Object getReceivedLinkCredit() {
        return getReceivedFields().get(Field.LINK_CREDIT);
    }

    public Object getReceivedAvailable() {
        return getReceivedFields().get(Field.AVAILABLE);
    }

    public Object getReceivedDrain() {
        return getReceivedFields().get(Field.DRAIN);
    }

    public Object getReceivedEcho() {
        return getReceivedFields().get(Field.ECHO);
    }

    public Object getReceivedProperties() {
        return getReceivedFields().get(Field.PROPERTIES);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
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
package org.apache.qpid.proton4j.engine.test.matchers.sections;

import java.util.HashMap;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.hamcrest.Matcher;

public class MessagePropertiesSectionMatcher extends MessageListSectionMatcher {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:properties:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000073L);

    /**
     * Note that the ordinals of the Field enums match the order specified in
     * the AMQP spec
     */
    public enum Field {
        MESSAGE_ID, USER_ID, TO, SUBJECT, REPLY_TO, CORRELATION_ID, CONTENT_TYPE, CONTENT_ENCODING, ABSOLUTE_EXPIRY_TIME, CREATION_TIME, GROUP_ID, GROUP_SEQUENCE, REPLY_TO_GROUP_ID,
    }

    public MessagePropertiesSectionMatcher(boolean expectTrailingBytes) {
        super(DESCRIPTOR_CODE, DESCRIPTOR_SYMBOL, new HashMap<Object, Matcher<?>>(), expectTrailingBytes);
    }

    public MessagePropertiesSectionMatcher withMessageId(Matcher<?> m) {
        getMatchers().put(Field.MESSAGE_ID, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withUserId(Matcher<?> m) {
        getMatchers().put(Field.USER_ID, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withTo(Matcher<?> m) {
        getMatchers().put(Field.TO, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withSubject(Matcher<?> m) {
        getMatchers().put(Field.SUBJECT, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withReplyTo(Matcher<?> m) {
        getMatchers().put(Field.REPLY_TO, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withCorrelationId(Matcher<?> m) {
        getMatchers().put(Field.CORRELATION_ID, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withContentType(Matcher<?> m) {
        getMatchers().put(Field.CONTENT_TYPE, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withContentEncoding(Matcher<?> m) {
        getMatchers().put(Field.CONTENT_ENCODING, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withAbsoluteExpiryTime(Matcher<?> m) {
        getMatchers().put(Field.ABSOLUTE_EXPIRY_TIME, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withCreationTime(Matcher<?> m) {
        getMatchers().put(Field.CREATION_TIME, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withGroupId(Matcher<?> m) {
        getMatchers().put(Field.GROUP_ID, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withGroupSequence(Matcher<?> m) {
        getMatchers().put(Field.GROUP_SEQUENCE, m);
        return this;
    }

    public MessagePropertiesSectionMatcher withReplyToGroupId(Matcher<?> m) {
        getMatchers().put(Field.REPLY_TO_GROUP_ID, m);
        return this;
    }

    public Object getReceivedMessageId() {
        return getReceivedFields().get(Field.MESSAGE_ID);
    }

    public Object getReceivedUserId() {
        return getReceivedFields().get(Field.USER_ID);
    }

    public Object getReceivedTo() {
        return getReceivedFields().get(Field.TO);
    }

    public Object getReceivedSubject() {
        return getReceivedFields().get(Field.SUBJECT);
    }

    public Object getReceivedReplyTo() {
        return getReceivedFields().get(Field.REPLY_TO);
    }

    public Object getReceivedCorrelationId() {
        return getReceivedFields().get(Field.CORRELATION_ID);
    }

    public Object getReceivedContentType() {
        return getReceivedFields().get(Field.CONTENT_TYPE);
    }

    public Object getReceivedContentEncoding() {
        return getReceivedFields().get(Field.CONTENT_ENCODING);
    }

    public Object getReceivedAbsoluteExpiryTime() {
        return getReceivedFields().get(Field.ABSOLUTE_EXPIRY_TIME);
    }

    public Object getReceivedCreationTime() {
        return getReceivedFields().get(Field.CREATION_TIME);
    }

    public Object getReceivedGroupId() {
        return getReceivedFields().get(Field.GROUP_ID);
    }

    public Object getReceivedGroupSequence() {
        return getReceivedFields().get(Field.GROUP_SEQUENCE);
    }

    public Object getReceivedReplyToGroupId() {
        return getReceivedFields().get(Field.REPLY_TO_GROUP_ID);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
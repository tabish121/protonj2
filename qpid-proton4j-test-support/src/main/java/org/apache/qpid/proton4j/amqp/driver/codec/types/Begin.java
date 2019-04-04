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

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.driver.codec.ListDescribedType;

public class Begin extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:begin:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000011L);

    private static final int FIELD_REMOTE_CHANNEL = 0;
    private static final int FIELD_NEXT_OUTGOING_ID = 1;
    private static final int FIELD_INCOMING_WINDOW = 2;
    private static final int FIELD_OUTGOING_WINDOW = 3;
    private static final int FIELD_HANDLE_MAX = 4;
    private static final int FIELD_OFFERED_CAPABILITIES = 5;
    private static final int FIELD_DESIRED_CAPABILITIES = 6;
    private static final int FIELD_PROPERTIES = 7;

    public Begin(Object... fields) {
        super(8);
        int i = 0;
        for (Object field : fields) {
            getFields()[i++] = field;
        }
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public Begin setRemoteChannel(Object o) {
        getFields()[FIELD_REMOTE_CHANNEL] = o;
        return this;
    }

    public Begin setNextOutgoingId(Object o) {
        getFields()[FIELD_NEXT_OUTGOING_ID] = o;
        return this;
    }

    public Begin setIncomingWindow(Object o) {
        getFields()[FIELD_INCOMING_WINDOW] = o;
        return this;
    }

    public Begin setOutgoingWindow(Object o) {
        getFields()[FIELD_OUTGOING_WINDOW] = o;
        return this;
    }

    public Begin setHandleMax(Object o) {
        getFields()[FIELD_HANDLE_MAX] = o;
        return this;
    }

    public Begin setOfferedCapabilities(Object o) {
        getFields()[FIELD_OFFERED_CAPABILITIES] = o;
        return this;
    }

    public Begin setDesiredCapabilities(Object o) {
        getFields()[FIELD_DESIRED_CAPABILITIES] = o;
        return this;
    }

    public Begin setProperties(Object o) {
        getFields()[FIELD_PROPERTIES] = o;
        return this;
    }
}
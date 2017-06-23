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
package org.apache.qpid.proton4j.amqp.messaging;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;

public final class DeleteOnNoMessages implements LifetimePolicy {

    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x000000000000002dL);
    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:delete-on-no-messages:list");

    private static final DeleteOnNoMessages INSTANCE = new DeleteOnNoMessages();

    private DeleteOnNoMessages() {
    }

    @Override
    public String toString() {
        return "DeleteOnNoMessages{}";
    }

    public static DeleteOnNoMessages getInstance() {
        return INSTANCE;
    }
}

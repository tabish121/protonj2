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
package org.apache.qpid.proton4j.amqp.driver.actions;

import org.apache.qpid.proton4j.amqp.driver.AMQPTestDriver;
import org.apache.qpid.proton4j.amqp.driver.ScriptedAction;
import org.apache.qpid.proton4j.amqp.transport.Performative;

/**
 * Abstract base used by inject actions of AMQP Performatives
 *
 * @param <P> the AMQP performative being sent.
 */
public abstract class AbstractPerformativeInjectAction<P extends Performative> implements ScriptedAction {

    public static final int CHANNEL_UNSET = -1;

    private int channel = CHANNEL_UNSET;

    public int onChannel() {
        return this.channel;
    }

    public AbstractPerformativeInjectAction<?> onChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public abstract P getPerformative();

    @Override
    public void perform(AMQPTestDriver driver) {
        driver.sendAMQPFrame(onChannel(), getPerformative(), null);
    }
}
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
package org.apache.qpid.proton4j.engine.impl;

import org.apache.qpid.proton4j.amqp.Binary;
import org.apache.qpid.proton4j.amqp.transport.DeliveryState;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.engine.Delivery;
import org.apache.qpid.proton4j.engine.IncomingDelivery;

/**
 * Proton Incoming Delivery implementation
 */
public class ProtonIncomingDelivery implements IncomingDelivery {

    private final ProtonContext context = new ProtonContext();
    private final Binary deliveryTag;
    private final ProtonReceiver link;

    private boolean complete;
    private int messageFormat;
    private boolean aborted;

    private DeliveryState defaultDeliveryState;

    private DeliveryState localState;
    private boolean locallySettled;

    private DeliveryState remoteState;
    private boolean remotelySettled;

    private ProtonBuffer payload;

    /**
     * @param link
     *      The link that this delivery is associated with
     * @param deliveryTag
     *      The delivery tag assigned to this delivery
     */
    public ProtonIncomingDelivery(ProtonReceiver link, Binary deliveryTag) {
        this.deliveryTag = deliveryTag;
        this.link = link;
    }

    @Override
    public ProtonReceiver getLink() {
        return link;
    }

    @Override
    public ProtonContext getContext() {
        return context;
    }

    @Override
    public byte[] getTag() {
        return deliveryTag.getArray();
    }

    @Override
    public DeliveryState getLocalState() {
        return localState;
    }

    @Override
    public DeliveryState getRemoteState() {
        return remoteState;
    }

    ProtonIncomingDelivery setRemoteState(DeliveryState state) {
        this.remoteState = state;
        return this;
    }

    @Override
    public int getMessageFormat() {
        return messageFormat;
    }

    ProtonIncomingDelivery setMessageFormat(int messageFormat) {
        this.messageFormat = messageFormat;
        return this;
    }

    @Override
    public boolean isPartial() {
        return !complete || aborted;
    }

    @Override
    public boolean isAborted() {
        return aborted;
    }

    @Override
    public boolean isSettled() {
        return locallySettled;
    }

    @Override
    public boolean isRemotelySettled() {
        return remotelySettled;
    }

    @Override
    public ProtonIncomingDelivery setDefaultDeliveryState(DeliveryState state) {
        this.defaultDeliveryState = state;
        return this;
    }

    @Override
    public DeliveryState getDefaultDeliveryState() {
        return defaultDeliveryState;
    }

    //----- TODO -- Move these to the Receiver so the delivery is acted upon consistently via its parent ?

    @Override
    public void disposition(DeliveryState state) {
        this.localState = state;
    }

    @Override
    public Delivery settle() {
        return settle(null);
    }

    @Override
    public ProtonIncomingDelivery settle(DeliveryState state) {
        this.locallySettled = true;
        this.localState = state;

        return this;
    }

    //----- Payload access

    @Override
    public int available() {
        return payload == null ? 0 : payload.getReadableBytes();
    }

    @Override
    public ProtonBuffer readAll() {
        ProtonBuffer result = payload;
        payload = null;
        return result;
    }

    @Override
    public ProtonIncomingDelivery readBytes(ProtonBuffer buffer) {
        if (payload != null) {
            payload.readBytes(buffer);
        }
        return this;
    }

    @Override
    public ProtonIncomingDelivery readBytes(byte[] array, int offset, int length) {
        if (payload != null) {
            payload.readBytes(array, offset, length);
        }
        return this;
    }

    //----- Internal methods to manage the Delivery

    ProtonIncomingDelivery aborted() {
        this.aborted = true;
        return this;
    }

    ProtonIncomingDelivery completed() {
        this.complete = true;
        return this;
    }

    ProtonIncomingDelivery remotelySettled() {
        this.remotelySettled = true;
        return this;
    }

    ProtonIncomingDelivery appendToPayload(ProtonBuffer buffer) {
        if (payload == null) {
            payload = buffer;
        } else {
            // TODO - CompositeBuffer to aggregate transfer payloads
            payload.writeBytes(buffer);
        }

        return this;
    }
}
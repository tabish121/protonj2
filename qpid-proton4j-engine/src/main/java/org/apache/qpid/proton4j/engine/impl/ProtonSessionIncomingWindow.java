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

import org.apache.qpid.proton4j.amqp.transport.Begin;
import org.apache.qpid.proton4j.amqp.transport.Flow;
import org.apache.qpid.proton4j.amqp.transport.Transfer;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;

/**
 * Tracks the incoming window and provides management of that window in relation to receiver links
 */
@SuppressWarnings("unused")
public class ProtonSessionIncomingWindow {

    private static final long DEFAULT_WINDOW_SIZE = Integer.MAX_VALUE; // biggest legal value

    private final ProtonSession session;

    // User configured incoming capacity for the session used to compute the incoming window
    private int incomingCapacity = 0;

    // These are used for the session windows communicated via Begin/Flow frames
    // and the conceptual transfer-id relating to updating them.
    private long incomingWindow = 0;
    private long nextIncomingId = -1;

    private long incomingDeliveryId = -1;
    private long remoteOutgoingWindow;
    private long remoteNextOutgoingId;

    private int incomingBytes;

    public ProtonSessionIncomingWindow(ProtonSession session) {
        this.session = session;
    }

    public void setIncomingCapaity(int incomingCapacity) {
        this.incomingCapacity = incomingCapacity;
    }

    public int getIncomingCapacity() {
        return incomingCapacity;
    }

    /**
     * Initialize the session level window values on the outbound Begin
     *
     * @param begin
     *      The {@link Begin} performative that is about to be sent.
     *
     * @return the configured performative
     */
    Begin configureOutbound(Begin begin) {
        return begin.setIncomingWindow(updateIncomingWindow());
    }

    /**
     * Update the session level window values based on remote information.
     *
     * @param begin
     *      The {@link Begin} performative received from the remote.
     *
     * @return the given performative for chaining
     */
    Begin processInbound(Begin begin) {
        this.remoteNextOutgoingId = begin.getNextOutgoingId();
        this.remoteOutgoingWindow = begin.getOutgoingWindow();

        return begin;
    }

    /**
     * Update the session window state based on an incoming {@link Flow} performative
     *
     * @param flow
     *      the incoming {@link Flow} performative to process.
     */
    Flow handleFlow(Flow flow) {
        this.remoteNextOutgoingId = flow.getNextOutgoingId();
        this.remoteOutgoingWindow = flow.getOutgoingWindow();

        return flow;
    }

    /**
     * Update the session window state based on an incoming {@link Transfer} performative
     *
     * @param transfer
     *      the incoming {@link Transfer} performative to process.
     */
    Transfer handleTransfer(Transfer transfer, ProtonBuffer payload) {
        if (payload != null && !transfer.getAborted()) {
            incomingBytes += payload.getReadableBytes();
        }

        incomingWindow--;

        return transfer;
    }

    long updateIncomingWindow() {
        // TODO - long vs int types for these unsigned value
        long maxFrameSize = session.getConnection().getMaxFrameSize();
        if (incomingCapacity <= 0 || maxFrameSize <= 0) {
            incomingWindow = DEFAULT_WINDOW_SIZE;
        } else {
            // TODO - incomingWindow = Integer.divideUnsigned(incomingCapacity - incomingBytes, maxFrameSize);
            incomingWindow = (incomingCapacity - incomingBytes) / maxFrameSize;
        }

        return incomingWindow;
    }

    void writeFlow(ProtonLink<?> link) {
        final Flow flow = new Flow();

        flow.setNextIncomingId(nextIncomingId);
        flow.setNextOutgoingId(session.getOutgoingWindow().getNextOutgoingId());
        flow.setIncomingWindow(incomingWindow);
        flow.setOutgoingWindow(session.getOutgoingWindow().getOutgoingWindow());

        if (link != null) {
            flow.setLinkCredit(link.getCreditState().getCredit());
            flow.setHandle(link.getHandle());
            flow.setDeliveryCount(link.getCreditState().getDeliveryCount());
        }
    }

    //----- Access to internal state useful for tests

    public long getIncomingBytes() {
        return incomingBytes;
    }

    public long getNextIncomingId() {
        return nextIncomingId;
    }

    public long getIncomingWindow() {
        return incomingWindow;
    }

    public long getRemoteNextOutgoingId() {
        return remoteNextOutgoingId;
    }

    public long getRemoteOutgoingWindow() {
        return remoteOutgoingWindow;
    }
}
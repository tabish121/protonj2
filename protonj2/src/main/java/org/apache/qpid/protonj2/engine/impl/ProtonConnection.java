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
package org.apache.qpid.protonj2.engine.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.engine.Connection;
import org.apache.qpid.protonj2.engine.ConnectionState;
import org.apache.qpid.protonj2.engine.EventHandler;
import org.apache.qpid.protonj2.engine.Receiver;
import org.apache.qpid.protonj2.engine.Sender;
import org.apache.qpid.protonj2.engine.Session;
import org.apache.qpid.protonj2.engine.SessionState;
import org.apache.qpid.protonj2.engine.TransactionManager;
import org.apache.qpid.protonj2.engine.exceptions.EngineFailedException;
import org.apache.qpid.protonj2.engine.exceptions.EngineStateException;
import org.apache.qpid.protonj2.engine.exceptions.ProtocolViolationException;
import org.apache.qpid.protonj2.types.Symbol;
import org.apache.qpid.protonj2.types.UnsignedInteger;
import org.apache.qpid.protonj2.types.transport.AMQPHeader;
import org.apache.qpid.protonj2.types.transport.AmqpError;
import org.apache.qpid.protonj2.types.transport.Attach;
import org.apache.qpid.protonj2.types.transport.Begin;
import org.apache.qpid.protonj2.types.transport.Close;
import org.apache.qpid.protonj2.types.transport.ConnectionError;
import org.apache.qpid.protonj2.types.transport.Detach;
import org.apache.qpid.protonj2.types.transport.Disposition;
import org.apache.qpid.protonj2.types.transport.End;
import org.apache.qpid.protonj2.types.transport.ErrorCondition;
import org.apache.qpid.protonj2.types.transport.Flow;
import org.apache.qpid.protonj2.types.transport.Open;
import org.apache.qpid.protonj2.types.transport.Performative;
import org.apache.qpid.protonj2.types.transport.Transfer;

/**
 * Implements the proton Connection API
 */
public class ProtonConnection extends ProtonEndpoint<Connection> implements Connection, AMQPHeader.HeaderHandler<ProtonEngine>, Performative.PerformativeHandler<ProtonEngine> {

    private final Open localOpen = new Open();
    private Open remoteOpen;
    private AMQPHeader remoteHeader;

    private Map<Integer, ProtonSession> localSessions = new HashMap<>();
    private Map<Integer, ProtonSession> remoteSessions = new HashMap<>();

    private ConnectionState localState = ConnectionState.IDLE;
    private ConnectionState remoteState = ConnectionState.IDLE;

    private boolean headerSent;
    private boolean headerReceived;
    private boolean localOpenSent;
    private boolean localCloseSent;

    private EventHandler<AMQPHeader> remoteHeaderHandler;
    private EventHandler<Session> remoteSessionOpenEventHandler;
    private EventHandler<Sender> remoteSenderOpenEventHandler;
    private EventHandler<Receiver> remoteReceiverOpenEventHandler;
    private EventHandler<TransactionManager> remoteTxnManagerOpenEventHandler;

    /**
     * Create a new unbound Connection instance.
     *
     * @param engine
     */
    ProtonConnection(ProtonEngine engine) {
        super(engine);

        // This configures the default for the client which could later be made configurable
        // by adding an option in EngineConfiguration but for now this is forced set here.
        this.localOpen.setMaxFrameSize(ProtonConstants.DEFAULT_MAX_AMQP_FRAME_SIZE);
    }

    @Override
    public Connection getParent() {
        return this;
    }

    @Override
    ProtonConnection self() {
        return this;
    }

    @Override
    public ConnectionState getState() {
        return localState;
    }

    @Override
    public ProtonConnection open() throws EngineStateException {
        if (getState() == ConnectionState.IDLE) {
            engine.checkShutdownOrFailed("Cannot open a connection when Engine is shutdown or failed.");
            localState = ConnectionState.ACTIVE;
            try {
                syncLocalStateWithRemote();
            } finally {
                fireLocalOpen();
            }
        }

        return this;
    }

    @Override
    public ProtonConnection close() throws EngineFailedException {
        if (getState() == ConnectionState.ACTIVE) {
            localState = ConnectionState.CLOSED;
            try {
                getEngine().checkFailed("Connection close called while engine .");
                syncLocalStateWithRemote();
            } finally {
                allSessions().forEach(session -> session.handleConnectionLocallyClosed(this));
                fireLocalClose();
            }
        }

        return this;
    }

    @Override
    public Connection negotiate() {
        checkConnectionClosed("Cannot start header negotiation on a closed connection");
        if (!headerSent) {
            fireAMQPHeader();
        }
        return this;
    }

    @Override
    public Connection negotiate(EventHandler<AMQPHeader> remoteAMQPHeaderHandler) {
        Objects.requireNonNull(remoteAMQPHeaderHandler, "Provided AMQP Header received handler cannot be null");
        checkConnectionClosed("Cannot start header negotiation on a closed connection");

        if (!headerSent) {
            fireAMQPHeader();
        }

        if (headerReceived) {
            remoteAMQPHeaderHandler.handle(remoteHeader);
        } else {
            remoteHeaderHandler = remoteAMQPHeaderHandler;
        }

        return this;
    }

    @Override
    public long tick(long current) {
        checkConnectionClosed("Cannot call tick on an already closed Connection");
        return engine.tick(current);
    }

    @Override
    public Connection tickAuto(ScheduledExecutorService executor) {
        checkConnectionClosed("Cannot call tickAuto on an already closed Connection");
        engine.tickAuto(executor);
        return this;
    }

    @Override
    public boolean isLocallyClosed() {
        return getState() == ConnectionState.CLOSED;
    }

    @Override
    public boolean isRemotelyClosed() {
        return getRemoteState() == ConnectionState.CLOSED;
    }

    @Override
    public ProtonConnection setContainerId(String containerId) {
        checkNotOpened("Cannot set Container Id on already opened Connection");
        localOpen.setContainerId(containerId);
        return this;
    }

    @Override
    public String getContainerId() {
        return localOpen.getContainerId();
    }

    @Override
    public ProtonConnection setHostname(String hostname) {
        checkNotOpened("Cannot set Hostname on already opened Connection");
        localOpen.setHostname(hostname);
        return this;
    }

    @Override
    public String getHostname() {
        return localOpen.getHostname();
    }

    @Override
    public Connection setMaxFrameSize(long maxFrameSize) {
        checkNotOpened("Cannot set Max Frame Size on already opened Connection");
        localOpen.setMaxFrameSize(maxFrameSize);
        return this;
    }

    @Override
    public long getMaxFrameSize() {
        return localOpen.getMaxFrameSize();
    }

    @Override
    public ProtonConnection setChannelMax(int channelMax) {
        checkNotOpened("Cannot set Channel Max on already opened Connection");
        localOpen.setChannelMax(channelMax);
        return this;
    }

    @Override
    public int getChannelMax() {
        return localOpen.getChannelMax();
    }

    @Override
    public ProtonConnection setIdleTimeout(long idleTimeout) {
        checkNotOpened("Cannot set Idle Timeout on already opened Connection");
        if (idleTimeout < 0 || idleTimeout > UnsignedInteger.MAX_VALUE.longValue()) {
            throw new IllegalArgumentException("Idle timeout cannot exceed the maximum value of an unsigned integer");
        }
        localOpen.setIdleTimeout(idleTimeout);
        return this;
    }

    @Override
    public long getIdleTimeout() {
        return localOpen.getIdleTimeout();
    }

    @Override
    public ProtonConnection setOfferedCapabilities(Symbol... capabilities) {
        checkNotOpened("Cannot set Offered Capabilities on already opened Connection");

        if (capabilities != null) {
            localOpen.setOfferedCapabilities(Arrays.copyOf(capabilities, capabilities.length));
        } else {
            localOpen.setOfferedCapabilities(capabilities);
        }

        return this;
    }

    @Override
    public Symbol[] getOfferedCapabilities() {
        if (localOpen.getOfferedCapabilities() != null) {
            return Arrays.copyOf(localOpen.getOfferedCapabilities(), localOpen.getOfferedCapabilities().length);
        }

        return null;
    }

    @Override
    public ProtonConnection setDesiredCapabilities(Symbol... capabilities) {
        checkNotOpened("Cannot set Desired Capabilities on already opened Connection");

        if (capabilities != null) {
            localOpen.setDesiredCapabilities(Arrays.copyOf(capabilities, capabilities.length));
        } else {
            localOpen.setDesiredCapabilities(capabilities);
        }

        return this;
    }

    @Override
    public Symbol[] getDesiredCapabilities() {
        if (localOpen.getDesiredCapabilities() != null) {
            return Arrays.copyOf(localOpen.getDesiredCapabilities(), localOpen.getDesiredCapabilities().length);
        }

        return null;
    }

    @Override
    public ProtonConnection setProperties(Map<Symbol, Object> properties) {
        checkNotOpened("Cannot set Properties on already opened Connection");

        if (properties != null) {
            localOpen.setProperties(new LinkedHashMap<>(properties));
        } else {
            localOpen.setProperties(properties);
        }

        return this;
    }

    @Override
    public Map<Symbol, Object> getProperties() {
        if (localOpen.getProperties() != null) {
            return Collections.unmodifiableMap(localOpen.getProperties());
        }

        return null;
    }

    @Override
    public boolean isLocallyOpen() {
        return getState() == ConnectionState.ACTIVE;
    }

    @Override
    public boolean isRemotelyOpen() {
        return getRemoteState() == ConnectionState.ACTIVE;
    }

    @Override
    public String getRemoteContainerId() {
        return remoteOpen == null ? null : remoteOpen.getContainerId();
    }

    @Override
    public String getRemoteHostname() {
        return remoteOpen == null ? null : remoteOpen.getHostname();
    }

    @Override
    public long getRemoteMaxFrameSize() {
        return remoteOpen == null ? ProtonConstants.MIN_MAX_AMQP_FRAME_SIZE : remoteOpen.getMaxFrameSize();
    }

    @Override
    public long getRemoteIdleTimeout() {
        return remoteOpen == null ? -1 : remoteOpen.getIdleTimeout();
    }

    @Override
    public Symbol[] getRemoteOfferedCapabilities() {
        if (remoteOpen != null && remoteOpen.getOfferedCapabilities() != null) {
            return Arrays.copyOf(remoteOpen.getOfferedCapabilities(), remoteOpen.getOfferedCapabilities().length);
        }

        return null;
    }

    @Override
    public Symbol[] getRemoteDesiredCapabilities() {
        if (remoteOpen != null && remoteOpen.getDesiredCapabilities() != null) {
            return Arrays.copyOf(remoteOpen.getDesiredCapabilities(), remoteOpen.getDesiredCapabilities().length);
        }

        return null;
    }

    @Override
    public Map<Symbol, Object> getRemoteProperties() {
        if (remoteOpen != null && remoteOpen.getProperties() != null) {
            return Collections.unmodifiableMap(remoteOpen.getProperties());
        }

        return null;
    }

    @Override
    public ConnectionState getRemoteState() {
        return remoteState;
    }

    @Override
    public ProtonSession session() throws IllegalStateException {
        checkConnectionClosed("Cannot create a Session from a Connection that is already closed");

        int localChannel = findFreeLocalChannel();
        ProtonSession newSession = new ProtonSession(this, localChannel);
        localSessions.put(localChannel, newSession);

        return newSession;
    }

    @Override
    public Set<Session> sessions() throws IllegalStateException {
        return Collections.unmodifiableSet(allSessions());
    }

    //----- Handle performatives sent from the remote to this Connection

    @Override
    public void handleAMQPHeader(AMQPHeader header, ProtonEngine context) {
        headerReceived = true;
        remoteHeader = header;

        if (remoteHeaderHandler != null) {
            remoteHeaderHandler.handle(remoteHeader);
            remoteHeaderHandler = null;
        }

        syncLocalStateWithRemote();
    }

    @Override
    public void handleSASLHeader(AMQPHeader header, ProtonEngine context) {
        context.engineFailed(new ProtocolViolationException("Receivded unexpected SASL Header"));
    }

    @Override
    public void handleOpen(Open open, ProtonBuffer payload, int channel, ProtonEngine context) {
        if (remoteOpen != null) {
            context.engineFailed(new ProtocolViolationException("Received second Open for Connection from remote"));
            return;
        }

        remoteState = ConnectionState.ACTIVE;
        remoteOpen = open;

        fireRemoteOpen();
    }

    @Override
    public void handleClose(Close close, ProtonBuffer payload, int channel, ProtonEngine context) {
        remoteState = ConnectionState.CLOSED;
        setRemoteCondition(close.getError());
        allSessions().forEach(session -> session.handleConnectionRemotelyClosed(this));

        fireRemoteClose();
    }

    @Override
    public void handleBegin(Begin begin, ProtonBuffer payload, int channel, ProtonEngine context) {
        ProtonSession session = null;

        if (channel > localOpen.getChannelMax()) {
            setCondition(new ErrorCondition(ConnectionError.FRAMING_ERROR, "Channel Max Exceeded for session Begin")).close();
        } else if (remoteSessions.containsKey(channel)) {
            context.engineFailed(new ProtocolViolationException("Received second begin for Session from remote"));
        } else {
            // If there is a remote channel then this is an answer to a local open of a session, otherwise
            // the remote is requesting a new session and we need to create one and signal that a remote
            // session was opened.
            if (begin.hasRemoteChannel()) {
                int remoteChannel = begin.getRemoteChannel();
                session = localSessions.get(begin.getRemoteChannel());
                if (session == null) {
                    setCondition(new ErrorCondition(AmqpError.PRECONDITION_FAILED, "No matching session found for remote channel given")).close();
                    engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Begin from remote: " + remoteChannel));
                    return;
                }
            } else {
                session = session();
            }

            remoteSessions.put(channel, session);

            // Let the session handle the remote Begin now.
            session.remoteBegin(begin, channel);

            // If the session was initiated remotely then we signal the creation to the any registered
            // remote session event handler
            if (session.getState() == SessionState.IDLE && remoteSessionOpenEventHandler != null) {
                remoteSessionOpenEventHandler.handle(session);
            }
        }
    }

    @Override
    public void handleEnd(End end, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.remove(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on End from remote: " + channel));
        } else {
            session.remoteEnd(end, channel);
        }
    }

    @Override
    public void handleAttach(Attach attach, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.get(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Attach from remote: " + channel));
        } else {
            session.remoteAttach(attach, channel);
        }
    }

    @Override
    public void handleDetach(Detach detach, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.get(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Detach from remote: " + channel));
        } else {
            session.remoteDetach(detach, channel);
        }
    }

    @Override
    public void handleFlow(Flow flow, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.get(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Flow from remote: " + channel));
        } else {
            session.remoteFlow(flow, channel);
        }
    }

    @Override
    public void handleTransfer(Transfer transfer, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.get(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Transfer from remote: " + channel));
        } else {
            session.remoteTransfer(transfer, payload, channel);
        }
    }

    @Override
    public void handleDisposition(Disposition disposition, ProtonBuffer payload, int channel, ProtonEngine context) {
        final ProtonSession session = remoteSessions.get(channel);
        if (session == null) {
            engine.engineFailed(new ProtocolViolationException("Received uncorrelated channel on Disposition from remote: " + channel));
        } else {
            session.remoteDispsotion(disposition, channel);
        }
    }

    //----- API for event handling of Connection related remote events

    @Override
    public ProtonConnection sessionOpenHandler(EventHandler<Session> remoteSessionOpenEventHandler) {
        this.remoteSessionOpenEventHandler = remoteSessionOpenEventHandler;
        return this;
    }

    @Override
    public ProtonConnection senderOpenHandler(EventHandler<Sender> remoteSenderOpenEventHandler) {
        this.remoteSenderOpenEventHandler = remoteSenderOpenEventHandler;
        return this;
    }

    EventHandler<Sender> senderOpenEventHandler() {
        return remoteSenderOpenEventHandler;
    }

    @Override
    public ProtonConnection receiverOpenHandler(EventHandler<Receiver> remoteReceiverOpenEventHandler) {
        this.remoteReceiverOpenEventHandler = remoteReceiverOpenEventHandler;
        return this;
    }

    EventHandler<Receiver> receiverOpenEventHandler() {
        return remoteReceiverOpenEventHandler;
    }

    @Override
    public ProtonConnection transactionManagerOpenHandler(EventHandler<TransactionManager> remoteTxnManagerOpenEventHandler) {
        this.remoteTxnManagerOpenEventHandler = remoteTxnManagerOpenEventHandler;
        return this;
    }

    EventHandler<TransactionManager> transactionManagerOpenHandler() {
        return remoteTxnManagerOpenEventHandler;
    }

    //----- Internal implementation

    private void checkNotOpened(String errorMessage) {
        if (localState.ordinal() > ConnectionState.IDLE.ordinal()) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private void checkConnectionClosed(String errorMessage) {
        if (isLocallyClosed() || isRemotelyClosed()) {
             throw new IllegalStateException(errorMessage);
        }
    }

    private void syncLocalStateWithRemote() {
        // When the engine state changes or we have read an incoming AMQP header etc we need to check
        // if we have pending work to send and do so
        if (headerSent) {
            final ConnectionState state = getState();

            // Once an incoming header arrives we can emit our open if locally opened and also send close if
            // that is what our state is already.
            if (state != ConnectionState.IDLE && headerReceived) {
                boolean resourceSyncNeeded = false;

                if (!localOpenSent && !engine.isShutdown()) {
                    localOpenSent = true;
                    resourceSyncNeeded = true;
                    engine.fireWrite(localOpen, 0, null, null);
                    engine.configuration().recomputeEffectiveFrameSizeLimits();
                }

                if (isLocallyClosed() && !localCloseSent && !engine.isShutdown()) {
                    localCloseSent = true;
                    resourceSyncNeeded = false;  // Session resources can't write anything now
                    Close localClose = new Close().setError(getCondition());
                    engine.fireWrite(localClose, 0, null, null);
                }

                if (resourceSyncNeeded) {
                    allSessions().forEach(session -> session.trySyncLocalStateWithRemote());
                }
            }
        } else if (!engine.isShutdown()) {
            fireAMQPHeader();
        }
    }

    void handleEngineShutdown(ProtonEngine protonEngine) {
        allSessions().forEach(session -> session.handleEngineShutdown(protonEngine));

        try {
            fireEngineShutdown();
        } catch (Exception ignore) {}
    }

    void handleEngineFailed(ProtonEngine protonEngine, Throwable cause) {
        if (localOpenSent && !localCloseSent) {
            localCloseSent = true;

            try {
                if (getCondition() == null) {
                    setCondition(errorConditionFromFailureCause(cause));
                }

                engine.fireWrite(new Close().setError(getCondition()), 0, null, null);
            } catch (Exception ignore) {}
        }
    }

    private ErrorCondition errorConditionFromFailureCause(Throwable cause) {
        final Symbol condition;
        final String description = cause.getMessage();

        if (cause instanceof ProtocolViolationException) {
            ProtocolViolationException error = (ProtocolViolationException) cause;
            condition = error.getErrorCondition();
        } else {
            condition = AmqpError.INTERNAL_ERROR;
        }

        return new ErrorCondition(condition, description);
    }

    private int findFreeLocalChannel() {
        for (int i = 0; i < ProtonConstants.CHANNEL_MAX; ++i) {
            if (!localSessions.containsKey(i)) {
                return i;
            }
        }

        throw new IllegalStateException("no local channel available for allocation");
    }

    @SuppressWarnings("unchecked")
    private Set<ProtonSession> allSessions() {
        final Set<ProtonSession> result;

        if (localSessions.isEmpty() && remoteSessions.isEmpty()) {
            result = Collections.EMPTY_SET;
        } else {
            result = new HashSet<>(localSessions.size());
            result.addAll(localSessions.values());
            result.addAll(remoteSessions.values());
        }

        return result;
    }

    void freeLocalChannel(int localChannel) {
        if (localChannel > ProtonConstants.CHANNEL_MAX) {
            throw new IllegalArgumentException("Specified local channel is out of range: " + localChannel);
        }

        localSessions.remove(localChannel);
    }

    void fireAMQPHeader() {
        headerSent = true;
        engine.fireWrite(AMQPHeader.getAMQPHeader());
    }

    boolean wasHeaderSent() {
        return this.headerSent;
    }

    boolean wasLocalOpenSent() {
        return this.localOpenSent;
    }

    boolean wasLocalCloseSent() {
        return this.localCloseSent;
    }
}
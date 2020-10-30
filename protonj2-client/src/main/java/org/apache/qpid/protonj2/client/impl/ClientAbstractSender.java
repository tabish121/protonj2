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
package org.apache.qpid.protonj2.client.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.client.AdvancedMessage;
import org.apache.qpid.protonj2.client.ErrorCondition;
import org.apache.qpid.protonj2.client.Message;
import org.apache.qpid.protonj2.client.Sender;
import org.apache.qpid.protonj2.client.SenderOptions;
import org.apache.qpid.protonj2.client.Source;
import org.apache.qpid.protonj2.client.Target;
import org.apache.qpid.protonj2.client.Tracker;
import org.apache.qpid.protonj2.client.exceptions.ClientConnectionRemotelyClosedException;
import org.apache.qpid.protonj2.client.exceptions.ClientException;
import org.apache.qpid.protonj2.client.exceptions.ClientIllegalStateException;
import org.apache.qpid.protonj2.client.exceptions.ClientOperationTimedOutException;
import org.apache.qpid.protonj2.client.exceptions.ClientResourceRemotelyClosedException;
import org.apache.qpid.protonj2.client.exceptions.ClientSendTimedOutException;
import org.apache.qpid.protonj2.client.exceptions.ClientUnsupportedOperationException;
import org.apache.qpid.protonj2.client.futures.ClientFuture;
import org.apache.qpid.protonj2.engine.Connection;
import org.apache.qpid.protonj2.engine.Engine;
import org.apache.qpid.protonj2.engine.LinkState;
import org.apache.qpid.protonj2.engine.OutgoingDelivery;
import org.apache.qpid.protonj2.types.transport.DeliveryState;
import org.apache.qpid.protonj2.types.transport.SenderSettleMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proton based AMQP Sender
 */
abstract class ClientAbstractSender implements Sender {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAbstractSender.class);

    protected static final AtomicIntegerFieldUpdater<ClientAbstractSender> CLOSED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(ClientAbstractSender.class, "closed");

    protected final ClientFuture<Sender> openFuture;
    protected final ClientFuture<Sender> closeFuture;

    protected volatile int closed;
    protected ClientException failureCause;

    protected final Deque<InFlightSend> blocked = new ArrayDeque<>();
    protected final SenderOptions options;
    protected final ClientSession session;
    protected final org.apache.qpid.protonj2.engine.Sender protonSender;
    protected final ScheduledExecutorService executor;
    protected final String senderId;
    protected Consumer<Sender> senderRemotelyClosedHandler;

    protected volatile Source remoteSource;
    protected volatile Target remoteTarget;

    public ClientAbstractSender(ClientSession session, SenderOptions options, String senderId, org.apache.qpid.protonj2.engine.Sender protonSender) {
        this.options = new SenderOptions(options);
        this.session = session;
        this.senderId = senderId;
        this.executor = session.getScheduler();
        this.openFuture = session.getFutureFactory().createFuture();
        this.closeFuture = session.getFutureFactory().createFuture();
        this.protonSender = protonSender.setLinkedResource(this);
    }

    @Override
    public String address() throws ClientException {
        final org.apache.qpid.protonj2.types.messaging.Target target;
        if (isDynamic()) {
            waitForOpenToComplete();
            target = protonSender.getRemoteTarget();
        } else {
            target = protonSender.getTarget();
        }

        if (target != null) {
            return target.getAddress();
        } else {
            return null;
        }
    }

    @Override
    public Source source() throws ClientException {
        waitForOpenToComplete();
        return remoteSource;
    }

    @Override
    public Target target() throws ClientException {
        waitForOpenToComplete();
        return remoteTarget;
    }

    @Override
    public ClientInstance client() {
        return session.client();
    }

    @Override
    public ClientConnection connection() {
        return session.connection();
    }

    @Override
    public ClientSession session() {
        return session;
    }

    @Override
    public ClientFuture<Sender> openFuture() {
        return openFuture;
    }

    @Override
    public void close() {
        try {
            doCloseOrDetach(true, null).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.interrupted();
        }
    }

    @Override
    public void close(ErrorCondition error) {
        Objects.requireNonNull(error, "Error Condition cannot be null");

        try {
            doCloseOrDetach(true, error).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.interrupted();
        }
    }

    @Override
    public void detach() {
        try {
            doCloseOrDetach(false, null).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.interrupted();
        }
    }

    @Override
    public void detach(ErrorCondition error) {
        Objects.requireNonNull(error, "Error Condition cannot be null");

        try {
            doCloseOrDetach(false, error).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.interrupted();
        }
    }

    @Override
    public ClientFuture<Sender> closeAsync() {
        return doCloseOrDetach(true, null);
    }

    @Override
    public ClientFuture<Sender> closeAsync(ErrorCondition error) {
        Objects.requireNonNull(error, "Error Condition cannot be null");

        return doCloseOrDetach(true, error);
    }

    @Override
    public ClientFuture<Sender> detachAsync() {
        return doCloseOrDetach(false, null);
    }

    @Override
    public ClientFuture<Sender> detachAsync(ErrorCondition error) {
        Objects.requireNonNull(error, "Error Condition cannot be null");

        return doCloseOrDetach(false, error);
    }

    private ClientFuture<Sender> doCloseOrDetach(boolean close, ErrorCondition error) {
        if (CLOSED_UPDATER.compareAndSet(this, 0, 1)) {
            executor.execute(() -> {
                if (protonSender.isLocallyOpen()) {
                    try {
                        protonSender.setCondition(ClientErrorCondition.asProtonErrorCondition(error));

                        if (close) {
                            protonSender.close();
                        } else {
                            protonSender.detach();
                        }
                    } catch (Throwable ignore) {
                        closeFuture.complete(this);
                    }
                }
            });
        }
        return closeFuture;
    }

    @Override
    public Map<String, Object> properties() throws ClientException {
        waitForOpenToComplete();
        return ClientConversionSupport.toStringKeyedMap(protonSender.getRemoteProperties());
    }

    @Override
    public String[] offeredCapabilities() throws ClientException {
        waitForOpenToComplete();
        return ClientConversionSupport.toStringArray(protonSender.getRemoteOfferedCapabilities());
    }

    @Override
    public String[] desiredCapabilities() throws ClientException {
        waitForOpenToComplete();
        return ClientConversionSupport.toStringArray(protonSender.getRemoteDesiredCapabilities());
    }

    @Override
    public Tracker send(Message<?> message) throws ClientException {
        checkClosedOrFailed();
        return sendMessage(ClientMessageSupport.convertMessage(message), null, true);
    }

    @Override
    public Tracker send(Message<?> message, Map<String, Object> deliveryAnnotations) throws ClientException {
        checkClosedOrFailed();
        return sendMessage(ClientMessageSupport.convertMessage(message), deliveryAnnotations, true);
    }

    @Override
    public Tracker trySend(Message<?> message) throws ClientException {
        checkClosedOrFailed();
        return sendMessage(ClientMessageSupport.convertMessage(message), null, false);
    }

    @Override
    public Tracker trySend(Message<?> message, Map<String, Object> deliveryAnnotations) throws ClientException {
        checkClosedOrFailed();
        return sendMessage(ClientMessageSupport.convertMessage(message), deliveryAnnotations, false);
    }

    //----- Internal API

    SenderOptions options() {
        return this.options;
    }

    Sender remotelyClosedHandler(Consumer<Sender> handler) {
        this.senderRemotelyClosedHandler = handler;
        return this;
    }

    void disposition(OutgoingDelivery delivery, DeliveryState state, boolean settled) throws ClientException {
        checkClosedOrFailed();
        executor.execute(() -> {
            delivery.disposition(state, settled);
        });
    }

    void abort(OutgoingDelivery delivery) throws ClientException {
        checkClosedOrFailed();
        executor.execute(() -> {
            delivery.abort();
        });
    }

    void complete(OutgoingDelivery delivery) throws ClientException {
        checkClosedOrFailed();
        executor.execute(() -> {
            delivery.streamBytes(null, true);
        });
    }

    ClientAbstractSender open() {
        protonSender.localOpenHandler(this::handleLocalOpen)
                    .localCloseHandler(this::handleLocalCloseOrDetach)
                    .localDetachHandler(this::handleLocalCloseOrDetach)
                    .openHandler(this::handleRemoteOpen)
                    .closeHandler(this::handleRemoteCloseOrDetach)
                    .detachHandler(this::handleRemoteCloseOrDetach)
                    .parentEndpointClosedHandler(this::handleParentEndpointClosed)
                    .creditStateUpdateHandler(this::handleCreditStateUpdated)
                    .engineShutdownHandler(this::handleEngineShutdown)
                    .open();

        return this;
    }

    void setFailureCause(ClientException failureCause) {
        this.failureCause = failureCause;
    }

    org.apache.qpid.protonj2.engine.Sender getProtonSender() {
        return protonSender;
    }

    ClientException getFailureCause() {
        if (failureCause == null) {
            return session.getFailureCause();
        } else {
            return failureCause;
        }
    }

    String getId() {
        return senderId;
    }

    boolean isClosed() {
        return closed > 0;
    }

    boolean isAnonymous() {
        return protonSender.<org.apache.qpid.protonj2.types.messaging.Target>getTarget().getAddress() == null;
    }

    boolean isDynamic() {
        return protonSender.getTarget() != null && protonSender.<org.apache.qpid.protonj2.types.messaging.Target>getTarget().isDynamic();
    }

    //----- Handlers for proton receiver events

    private void handleLocalOpen(org.apache.qpid.protonj2.engine.Sender sender) {
        if (options.openTimeout() > 0) {
            executor.schedule(() -> {
                if (!openFuture.isDone()) {
                    immediateLinkShutdown(new ClientOperationTimedOutException("Sender open timed out waiting for remote to respond"));
                }
            }, options.openTimeout(), TimeUnit.MILLISECONDS);
        }
    }

    private void handleLocalCloseOrDetach(org.apache.qpid.protonj2.engine.Sender sender) {
        // If not yet remotely closed we only wait for a remote close if the engine isn't
        // already failed and we have successfully opened the sender without a timeout.
        if (!sender.getEngine().isShutdown() && failureCause == null && sender.isRemotelyOpen()) {
            final long timeout = options.closeTimeout();

            if (timeout > 0) {
                session.scheduleRequestTimeout(closeFuture, timeout, () ->
                    new ClientOperationTimedOutException("Sender close timed out waiting for remote to respond"));
            }
        } else {
            immediateLinkShutdown(failureCause);
        }
    }

    private void handleParentEndpointClosed(org.apache.qpid.protonj2.engine.Sender sender) {
        final ClientException failureCause;

        if (sender.getConnection().getRemoteCondition() != null) {
            failureCause = ClientExceptionSupport.convertToConnectionClosedException(sender.getConnection().getRemoteCondition());
        } else if (sender.getSession().getRemoteCondition() != null) {
            failureCause = ClientExceptionSupport.convertToSessionClosedException(sender.getSession().getRemoteCondition());
        } else if (sender.getEngine().failureCause() != null) {
            failureCause = ClientExceptionSupport.convertToConnectionClosedException(sender.getEngine().failureCause());
        } else if (!isClosed()) {
            failureCause = new ClientResourceRemotelyClosedException("Remote closed without a specific error condition");
        } else {
            failureCause = null;
        }

        immediateLinkShutdown(failureCause);
    }

    private void handleRemoteOpen(org.apache.qpid.protonj2.engine.Sender sender) {
        // Check for deferred close pending and hold completion if so
        if (sender.getRemoteTarget() != null) {
            remoteSource = new RemoteSource(sender.getRemoteSource());

            if (sender.getRemoteTarget() != null) {
                remoteTarget = new RemoteTarget(sender.getRemoteTarget());
            }

            openFuture.complete(this);
            LOG.trace("Sender opened successfully");
        } else {
            LOG.debug("Sender opened but remote signalled close is pending: ", sender);
        }
    }

    private void handleRemoteCloseOrDetach(org.apache.qpid.protonj2.engine.Sender sender) {
        if (sender.isLocallyOpen()) {
            try {
                senderRemotelyClosedHandler.accept(this);
            } catch (Throwable ignore) {}

            immediateLinkShutdown(ClientExceptionSupport.convertToLinkClosedException(
                sender.getRemoteCondition(), "Sender remotely closed without explanation from the remote"));
        } else {
            immediateLinkShutdown(failureCause);
        }
    }

    private void handleCreditStateUpdated(org.apache.qpid.protonj2.engine.Sender sender) {
        if (!blocked.isEmpty()) {
            while (sender.isSendable() && !blocked.isEmpty()) {
                LOG.trace("Dispatching previously held send");
                final InFlightSend held = blocked.poll();
                try {
                    assumeSendableAndSend(held);
                } catch (Exception ex) {
                    held.failed(ClientExceptionSupport.createNonFatalOrPassthrough(ex));
                }
            }
        }

        if (sender.isDraining() && blocked.isEmpty()) {
            sender.drained();
        }
    }

    private void handleEngineShutdown(Engine engine) {
        final Connection connection = engine.connection();

        final ClientException failureCause;

        if (connection.getRemoteCondition() != null) {
            failureCause = ClientExceptionSupport.convertToConnectionClosedException(connection.getRemoteCondition());
        } else if (engine.failureCause() != null) {
            failureCause = ClientExceptionSupport.convertToConnectionClosedException(engine.failureCause());
        } else if (!isClosed()) {
            failureCause = new ClientConnectionRemotelyClosedException("Remote closed without a specific error condition");
        } else {
            failureCause = null;
        }

        immediateLinkShutdown(failureCause);
    }

    void handleAnonymousRelayNotSupported() {
        if (isAnonymous() && protonSender.getState() == LinkState.IDLE) {
            immediateLinkShutdown(new ClientUnsupportedOperationException("Anonymous relay support not available from this connection"));
        }
    }

    //----- Private implementation details

    private void waitForOpenToComplete() throws ClientException {
        if (!openFuture.isComplete() || openFuture.isFailed()) {
            try {
                openFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                Thread.interrupted();
                if (failureCause != null) {
                    throw failureCause;
                } else {
                    throw ClientExceptionSupport.createNonFatalOrPassthrough(e.getCause());
                }
            }
        }
    }

    protected final void addToTailOfBlockedQueue(InFlightSend send) {
        if (options.sendTimeout() > 0 && send.timeout() == null) {
            send.timeout(executor.schedule(() -> {
                send.failed(send.createSendTimedOutException());
            }, options.sendTimeout(), TimeUnit.MILLISECONDS));
        }

        blocked.addLast(send);
    }

    protected final void addToHeadOfBlockedQueue(InFlightSend send) {
        if (options.sendTimeout() > 0 && send.timeout() == null) {
            send.timeout(executor.schedule(() -> {
                send.failed(send.createSendTimedOutException());
            }, options.sendTimeout(), TimeUnit.MILLISECONDS));
        }

        blocked.addFirst(send);
    }

    protected abstract Tracker sendMessage(AdvancedMessage<?> message, Map<String, Object> deliveryAnnotations, boolean waitForCredit) throws ClientException;

    protected final void assumeSendableAndSend(InFlightSend send) {
        final OutgoingDelivery delivery = send.delivery();

        if (session.getTransactionContext().isInTransaction()) {
            if (session.getTransactionContext().isTransactionInDoubt()) {
                send.ignored();
                return;
            } else {
                delivery.disposition(session.getTransactionContext().enlistSendInCurrentTransaction(this, delivery),
                    protonSender.getSenderSettleMode() == SenderSettleMode.SETTLED || delivery.isSettled());
            }
        } else {
            delivery.disposition(delivery.getState(), protonSender.getSenderSettleMode() == SenderSettleMode.SETTLED || delivery.isSettled());
        }

        if (send.aborted()) {
            delivery.abort();
            send.succeeded();
        } else {
            // We must check if the delivery was fully written and then complete the send operation otherwise
            // if the session capacity limited the amount of payload data we need to hold the completion until
            // the session capacity is refilled and we can fully write the remaining message payload.  This
            // area could use some enhancement to allow control of write and flush when dealing with delivery
            // modes that have low assurance versus those that are strict.
            delivery.streamBytes(send.encodedMessage(), send.complete());
            if (send.encodedMessage().isReadable()) {
                addToHeadOfBlockedQueue(send);
            } else {
                send.succeeded();
            }
        }
    }

    protected void checkClosedOrFailed(ClientFuture<?> request) {
        if (isClosed()) {
            request.failed(new ClientIllegalStateException("The Sender was explicity closed", failureCause));
        } else if (failureCause != null) {
            request.failed(failureCause);
        }
    }

    protected void checkClosedOrFailed() throws ClientException {
        if (isClosed()) {
            throw new ClientIllegalStateException("The Sender was explicity closed", failureCause);
        } else if (failureCause != null) {
            throw failureCause;
        }
    }

    private void immediateLinkShutdown(ClientException failureCause) {
        if (this.failureCause == null) {
            this.failureCause = failureCause;
        }

        try {
            if (protonSender.isRemotelyDetached()) {
                protonSender.detach();
            } else {
                protonSender.close();
            }
        } catch (Throwable ignore) {
        }

        // Cancel all blocked sends passing an appropriate error to the future
        blocked.removeIf((held) -> {
            if (failureCause != null) {
                held.failed(failureCause);
            } else {
                held.failed(new ClientResourceRemotelyClosedException("The sender link has closed"));
            }

            return true;
        });

        protonSender.unsettled().forEach((delivery) -> {
            try {
                final ClientTracker tracker = delivery.getLinkedResource();
                if (failureCause != null) {
                    tracker.settlementFuture().failed(failureCause);
                } else {
                    tracker.settlementFuture().failed(new ClientResourceRemotelyClosedException("The sender link has closed"));
                }
            } catch (Exception e) {
            }
        });

        if (failureCause != null) {
            openFuture.failed(failureCause);
        } else {
            openFuture.complete(this);
        }

        closeFuture.complete(this);
    }

    //----- Send Result Tracker used for send blocked on credit

    protected interface InFlightSend {

        ScheduledFuture<?> timeout();

        void timeout(ScheduledFuture<?> sendTimeout);

        ProtonBuffer encodedMessage();

        OutgoingDelivery delivery();

        Tracker tracker();

        boolean aborted();

        boolean complete();

        void ignored();

        void succeeded();

        InFlightSend failed(ClientException exception);

        default ClientException createSendTimedOutException() {
            return new ClientSendTimedOutException("Timed out waiting for credit to send");
        }
    }
}
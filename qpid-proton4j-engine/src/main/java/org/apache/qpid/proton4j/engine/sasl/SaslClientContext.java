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
package org.apache.qpid.proton4j.engine.sasl;

import org.apache.qpid.proton4j.amqp.Binary;
import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.engine.Connection;
import org.apache.qpid.proton4j.engine.Engine;

/**
 * SASL Client operating context used by an {@link Engine} that has been
 * configured as a SASL client or that has initialed the SASL exchange by
 * being the first to initiate the AMQP header exchange.
 */
public interface SaslClientContext extends SaslContext {

    /**
     * Sets the {@link SaslClientListener} that will be used to driver the client side SASL
     * negotiations with a connected "server".  As the server initiates or responds to the
     * various phases of the SASL negotiation the {@link SaslClientListener} will be notified
     * and allowed to respond.
     *
     * @param listener
     *      The {@link SaslClientListener} to use for SASL negotiations, cannot be null.
     *
     * @return this client context.
     */
    SaslClientContext setListener(SaslClientListener listener);

    /**
     * @return the currently set {@link SaslClientListener} instance.
     */
    SaslClientListener getListener();

    //----- SASL Negotiation API

    /**
     * Sends the AMQP Header indicating the desire for SASL negotiations to be commenced on
     * this connection.  The hosting application my wish to start SASL negotiations prior to
     * opening a {@link Connection} in order to validation authentication state out of band
     * of the normal open process.
     *
     * @return this client context.
     */
    SaslClientContext sendSASLHeader();

    /**
     * Sends a response to the SASL server indicating the chosen mechanism for this
     * client and the host name that this client is identifying itself as.
     *
     * @param mechanism
     *      The chosen mechanism selected from the list the server provided.
     * @param host
     *      The host name that the client is identified as or null if none selected.
     * @param initialResponse
     *      The initial response data sent as defined by the chosen mechanism or null if none required.
     *
     * @return this client context.
     */
    SaslClientContext sendChosenMechanism(Symbol mechanism, String host, Binary initialResponse);

    /**
     * Sends a response to a server side challenge that comprises the challenge / response
     * exchange for the chosen SASL mechanism.
     *
     * @param response
     *      The response bytes to be sent to the server for this cycle.
     *
     * @return this client context.
     */
    SaslClientContext sendResponse(Binary response);

}
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
package org.apache.qpid.proton4j.engine;

import java.util.Map;

import org.apache.qpid.proton4j.amqp.Symbol;

/**
 * AMQP Connection state container
 */
public interface Connection extends Endpoint {

    // TODO - Context feature, do we want something more that just one element of context ?
    //
    // void setContext(Object context);
    // Object getContext();
    // void setContext(String key, Object value);
    // Object getContext(String key);

    //----- Operations on local end of this Connection

    /**
     * Creates a new Session linked to this Connection
     */
    Session session();

    // TODO - Define exception thrown if already opened and a set is called.

    /**
     * @returns the Container ID assigned to this Connection
     */
    String getContainerId();

    /**
     * Sets the Container Id to be used when opening this Connection.
     *
     * @param containerId
     *      The Container Id used for this end of the Connection.
     */
    void setContainerId(String containerId);

    /**
     * Set the name of the host (either fully qualified or relative) to which
     * this connection is connecting to.  This information may be used by the
     * remote peer to determine the correct back-end service to connect the
     * client to.  This value will be sent in the Open performative.
     *
     * <b>Note that it is illegal to set the hostname to a numeric IP
     * address or include a port number.</b>
     *
     * @param hostname the RFC1035 compliant host name.
     */
    public void setHostname(String hostname);

    /**
     * @return returns the host name assigned to this Connection.
     *
     * @see setHostname
     */
    public String getHostname();

    /**
     * Sets the capabilities to be offered on to the remote when this Connection is
     * opened.
     *
     * @param capabilities
     *      The capabilities to be offered to the remote when the Connection is opened.
     */
    void setOfferedCapabilities(Symbol[] capabilities);

    /**
     * @return the configured capabilities that are offered to the remote when the Connection is opened.
     */
    Symbol[] getOfferedCapabilities();

    /**
     * Sets the capabilities that are desired from the remote when this Connection is
     * opened.
     *
     * @param capabilities
     *      The capabilities desired from the remote when the Connection is opened.
     */
    void setDesiredCapabilities(Symbol[] capabilities);

    /**
     * @return the configured desired capabilities that are sent to the remote when the Connection is opened.
     */
    Symbol[] getDesiredCapabilities();

    /**
     * Sets the properties to be sent to the remote when this Connection is Opened.
     *
     * @param properties
     *      The properties that will be sent to the remote when this Connection is opened.
     */
    void setProperties(Map<Symbol, Object> properties);

    /**
     * @return the configured properties sent to the remote when this Connection is opened.
     */
    Map<Symbol, Object> getProperties();

    //----- View state of remote end of this Connection

    // TODO - Define exception thrown if not yet remotely opened ?

    /**
     * @return the Container Id assigned to the remote end of the Connection.
     */
    String getRemoteContainerId();

    /**
     * @return the host name assigned to the remote end of this Connection.
     */
    String getRemoteHostname();

    /**
     * @return the capabilities offered by the remote when it opened its end of the Connection.
     */
    Symbol[] getRemoteOfferedCapabilities();

    /**
     * @return the capabilities desired by the remote when it opened its end of the Connection.
     */
    Symbol[] getRemoteDesiredCapabilities();

    /**
     * @return the properties sent by the remote when it opened its end of the Connection.
     */
    Map<Symbol, Object> getRemoteProperties();

}
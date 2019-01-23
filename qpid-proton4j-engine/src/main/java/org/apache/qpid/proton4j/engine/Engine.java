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

import org.apache.qpid.proton4j.buffer.ProtonBufferAllocator;

/**
 * AMQP Engine interface.
 */
public interface Engine {

    // TODO

    Connection start();

    void shutdown();

    /**
     * Sets the ProtonBufferAllocator used by this Transport.
     * <p>
     * When copying data, encoding types or otherwise needing to allocate memory
     * storage the Transport will use the assigned {@link ProtonBufferAllocator}.
     * If no allocator is assigned the Transport will use the default allocator.
     *
     * @param allocator
     *      The Allocator instance to use from this Transport.
     */
    void setBufferAllocator(ProtonBufferAllocator allocator);

    /**
     * @return the currently assigned {@link ProtonBufferAllocator}.
     */
    ProtonBufferAllocator getBufferAllocator();

    /**
     * Sets a TransportListener to be notified of events on this Transport
     *
     * @param listener
     *      The TransportListener to notify
     */
    void setTransportListener(EngineListener listener);

    /**
     * Gets the currently configured TransportListener instance.
     *
     * @return the currently configured TransportListener.
     */
    EngineListener getTransportListener();

    /**
     * Gets the TransportPipeline for this Transport.
     *
     * @return the {@link EnginePipeline} for this {@link Engine}.
     */
    EnginePipeline getPipeline();

    /**
     * Gets the Configuration for this engine.
     *
     * @return the configuration object for this engine.
     */
    EngineConfiguration getConfiguration();

}
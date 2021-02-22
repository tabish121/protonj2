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
package org.apache.qpid.protonj2.engine;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.engine.util.RingQueue;
import org.apache.qpid.protonj2.types.transport.Performative;

/**
 * Pooled of ProtocolFrame instances used to reduce allocations on incoming frames.
 */
public class ProtocolFramePool {

    public static final int DEFAULT_MAX_POOL_SIZE = 10;

    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    private RingQueue<ProtocolFrame> pool;

    public ProtocolFramePool() {
        this(ProtocolFramePool.DEFAULT_MAX_POOL_SIZE);
    }

    public ProtocolFramePool(int maxPoolSize) {
        this.pool = new RingQueue<>(getMaxPoolSize());
    }

    public final int getMaxPoolSize() {
        return maxPoolSize;
    }

    public ProtocolFrame take(Performative body, int channel, int frameSize, ProtonBuffer payload) {
        return (ProtocolFrame) pool.poll(this::newProtocolFrame).initialize(body, channel, frameSize, payload);
    }

    private ProtocolFrame newProtocolFrame() {
        return new ProtocolFrame(this);
    }

    void release(ProtocolFrame pooledFrame) {
        pool.offer(pooledFrame);
    }
}

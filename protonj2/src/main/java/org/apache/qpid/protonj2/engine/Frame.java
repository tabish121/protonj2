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

/**
 * Base class for Frames that travel through the engine.
 *
 * @param <V> The type of frame body that this {@link Frame} will carry.
 */
public abstract class Frame<V> {

    private final byte type;

    private V body;
    private int channel;
    private int frameSize;
    private ProtonBuffer payload;

    protected Frame(byte type) {
        this.type = type;
    }

    void initialize(V body, int channel, int frameSize, ProtonBuffer payload) {
        this.body = body;
        this.channel = channel;
        this.frameSize = frameSize;
        this.payload = payload;
    }

    /**
     * @return the decoded body of the frame.
     */
    public V getBody() {
        return body;
    }

    /**
     * @return the channel that the frame was sent on
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @return the encoded size that this frame occupied
     */
    public int getFrameSize() {
        return frameSize;
    }

    /**
     * @return the type that is assigned to this frame
     */
    public byte getType() {
        return type;
    }

    /**
     * @return the binary payload that was delivered with this frame
     */
    public ProtonBuffer getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Frame:[" + body + ", " + channel + ", " + frameSize + ", " + payload + "]";
    }
}
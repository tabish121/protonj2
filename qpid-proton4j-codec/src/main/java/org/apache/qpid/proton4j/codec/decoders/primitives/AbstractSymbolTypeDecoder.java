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
package org.apache.qpid.proton4j.codec.decoders.primitives;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.codec.DecoderState;

/**
 * Base class for the Symbol decoders used on AMQP Symbol types.
 */
public abstract class AbstractSymbolTypeDecoder implements SymbolTypeDecoder {

    @Override
    public Symbol readValue(ProtonBuffer buffer, DecoderState state) throws IOException {
        int length = readSize(buffer);

        byte[] bytes = new byte[length];
        buffer.readBytes(bytes, 0, length);

        return state.getSymbol(bytes);
    }

    @Override
    public void skipValue(ProtonBuffer buffer, DecoderState state) throws IOException {
        buffer.skipBytes(readSize(buffer));
    }

    protected abstract int readSize(ProtonBuffer buffer);

}

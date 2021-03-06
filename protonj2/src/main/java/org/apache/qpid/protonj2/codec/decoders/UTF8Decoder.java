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
package org.apache.qpid.protonj2.codec.decoders;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;

/**
 * Interface for an external UTF8 Decoder that can be supplied by a client
 * which implements custom decoding logic optimized for the application using
 * the Codec.
 */
public interface UTF8Decoder {

    /**
     * Decodes a String from the given UTF8 Bytes advancing the buffer read index
     * by the given length value once complete.  If the implementation does not advance
     * the buffer read index the outcome of future decode calls is not defined.
     *
     * @param buffer
     *      A ProtonBuffer containing the UTF-8 encoded bytes.
     * @param utf8length
     *      The number of bytes in the passed buffer that comprise the UTF-8 encoded value.
     *
     * @return a new String that represents the decoded value.
     */
    String decodeUTF8(ProtonBuffer buffer, int utf8length);

}

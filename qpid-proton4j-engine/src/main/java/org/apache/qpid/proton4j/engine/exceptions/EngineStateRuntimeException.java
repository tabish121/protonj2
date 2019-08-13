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
package org.apache.qpid.proton4j.engine.exceptions;

/**
 * Thrown from methods that cannot throw an EngineStateException directly.
 */
public class EngineStateRuntimeException extends ProtonRuntimeException {

    private static final long serialVersionUID = -8697429498705441964L;

    public EngineStateRuntimeException(EngineStateException cause) {
        super(cause);
    }

    public EngineStateRuntimeException(String message, EngineStateException cause) {
        super(message, cause);
    }

    @Override
    public EngineStateException getCause() {
        return (EngineStateException) super.getCause();
    }
}
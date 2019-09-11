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

/**
 * Represents the outcome of a SASL exchange
 */
public enum SaslOutcome {

    /** authentication succeeded */
    SASL_OK,
    /** failed due to bad credentials */
    SASL_AUTH,
    /** failed due to a system error */
    SASL_SYS,
    /** failed due to unrecoverable error */
    SASL_PERM,
    /** failed due to transient error */
    SASL_TEMP;

    /**
     * Return a matching SASL Outcome from the given byte value.
     *
     * @param outcome
     *      The byte value that is to be mapped to a SASL Outcome.
     *
     * @return the {@link SaslOutcome} that matches the given value.
     *
     * @throws IllegalArgumentException if the given outcome value is unknown.
     */
    public static SaslOutcome valueOf(byte outcome) {
        switch (outcome) {
            case 0:
                return SASL_OK;
            case 1:
                return SASL_AUTH;
            case 2:
                return SASL_SYS;
            case 3:
                return SASL_PERM;
            case 4:
                return SASL_TEMP;
            default:
                throw new IllegalArgumentException("Unknown SASL outcome code provided");
        }
    }
}

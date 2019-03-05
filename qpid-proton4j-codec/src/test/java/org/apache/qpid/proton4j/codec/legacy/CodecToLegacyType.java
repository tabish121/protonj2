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
package org.apache.qpid.proton4j.codec.legacy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.qpid.proton4j.amqp.Binary;
import org.apache.qpid.proton4j.amqp.Decimal128;
import org.apache.qpid.proton4j.amqp.Decimal32;
import org.apache.qpid.proton4j.amqp.Decimal64;
import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedByte;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.amqp.UnsignedShort;
import org.apache.qpid.proton4j.amqp.messaging.TerminusDurability;
import org.apache.qpid.proton4j.amqp.messaging.TerminusExpiryPolicy;
import org.apache.qpid.proton4j.amqp.transport.Attach;
import org.apache.qpid.proton4j.amqp.transport.Begin;
import org.apache.qpid.proton4j.amqp.transport.Open;
import org.apache.qpid.proton4j.amqp.transport.ReceiverSettleMode;
import org.apache.qpid.proton4j.amqp.transport.Role;
import org.apache.qpid.proton4j.amqp.transport.SenderSettleMode;

/**
 * Set of methods for converting from Codec types to legacy proton-j types
 * for use with the legacy codec when testing the codec.
 */
public abstract class CodecToLegacyType {

    public static Object convertToLegacyType(Object newType) {

        // Basic Types
        if (newType instanceof UnsignedByte) {
            return convertToLegacyType((UnsignedByte) newType);
        } else if (newType instanceof UnsignedShort) {
            return convertToLegacyType((UnsignedShort) newType);
        } else if (newType instanceof UnsignedInteger) {
            return convertToLegacyType((UnsignedInteger) newType);
        } else if (newType instanceof UnsignedLong) {
            return convertToLegacyType((UnsignedLong) newType);
        } else if (newType instanceof Binary) {
            return convertToLegacyType((Binary) newType);
        } else if (newType instanceof Symbol) {
            return convertToLegacyType((Symbol) newType);
        } else if (newType instanceof Decimal32) {
            return convertToLegacyType((Decimal32) newType);
        } else if (newType instanceof Decimal64) {
            return convertToLegacyType((Decimal64) newType);
        } else if (newType instanceof Decimal128) {
            return convertToLegacyType((Decimal128) newType);
        }

        // Arrays, Maps and Lists
        if (newType instanceof Map) {
            return convertToLegacyType((Map<?, ?>) newType);
        } // TODO

        // Enumerations
        if (newType instanceof Role) {
            return convertToLegacyType((Role) newType);
        } else if (newType instanceof SenderSettleMode) {
            return convertToLegacyType((SenderSettleMode) newType);
        } else if (newType instanceof ReceiverSettleMode) {
            return convertToLegacyType((ReceiverSettleMode) newType);
        } else if (newType instanceof TerminusDurability) {
            return convertToLegacyType((TerminusDurability) newType);
        } else if (newType instanceof TerminusExpiryPolicy) {
            return convertToLegacyType((TerminusExpiryPolicy) newType);
        }

        // Messaging Types

        // Transaction Types

        // Transport Types
        if (newType instanceof Open) {
            return convertToLegacyType((Open) newType);
        } else if (newType instanceof Begin) {
            return convertToLegacyType((Begin) newType);
        } else if (newType instanceof Attach) {
            return convertToLegacyType((Attach) newType);
        }

        // Security Types

        return newType;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param open
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.Open convertToLegacyType(Open open) {
        org.apache.qpid.proton.amqp.transport.Open legacyOpen = new org.apache.qpid.proton.amqp.transport.Open();

        legacyOpen.setContainerId(open.getContainerId());
        legacyOpen.setHostname(open.getHostname());
        if (open.getMaxFrameSize() != null) {
            legacyOpen.setMaxFrameSize(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(open.getMaxFrameSize().intValue()));
        }
        if (open.getChannelMax() != null) {
            legacyOpen.setChannelMax(org.apache.qpid.proton.amqp.UnsignedShort.valueOf(open.getChannelMax().shortValue()));
        }
        if (open.getIdleTimeOut() != null) {
            legacyOpen.setIdleTimeOut(convertToLegacyType(open.getIdleTimeOut()));
        }
        if (open.getOutgoingLocales() != null) {
            legacyOpen.setOutgoingLocales(convertToLegacyType(open.getOutgoingLocales()));
        }
        if (open.getIncomingLocales() != null) {
            legacyOpen.setIncomingLocales(convertToLegacyType(open.getIncomingLocales()));
        }
        if (open.getOfferedCapabilities() != null) {
            legacyOpen.setOfferedCapabilities(convertToLegacyType(open.getOfferedCapabilities()));
        }
        if (open.getDesiredCapabilities() != null) {
            legacyOpen.setDesiredCapabilities(convertToLegacyType(open.getDesiredCapabilities()));
        }
        if (open.getProperties() != null) {
            legacyOpen.setProperties(convertToLegacyType(open.getProperties()));
        }

        return legacyOpen;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param begin
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.Begin convertToLegacyType(Begin begin) {
        org.apache.qpid.proton.amqp.transport.Begin legacyBegin = new org.apache.qpid.proton.amqp.transport.Begin();

        if (begin.hasHandleMax()) {
            legacyBegin.setHandleMax(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(begin.getHandleMax()));
        }
        if (begin.hasIncomingWindow()) {
            legacyBegin.setIncomingWindow(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(begin.getIncomingWindow()));
        }
        if (begin.hasNextOutgoingId()) {
            legacyBegin.setNextOutgoingId(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(begin.getNextOutgoingId()));
        }
        if (begin.hasOutgoingWindow()) {
            legacyBegin.setOutgoingWindow(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(begin.getOutgoingWindow()));
        }
        if (begin.hasRemoteChannel()) {
            legacyBegin.setRemoteChannel(org.apache.qpid.proton.amqp.UnsignedShort.valueOf((short) begin.getRemoteChannel()));
        }
        if (begin.hasOfferedCapabilites()) {
            legacyBegin.setOfferedCapabilities(convertToLegacyType(begin.getOfferedCapabilities()));
        }
        if (begin.hasDesiredCapabilites()) {
            legacyBegin.setDesiredCapabilities(convertToLegacyType(begin.getDesiredCapabilities()));
        }
        if (begin.hasProperties()) {
            legacyBegin.setProperties(convertToLegacyType(begin.getProperties()));
        }

        return legacyBegin;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param attach
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.Attach convertToLegacyType(Attach attach) {
        org.apache.qpid.proton.amqp.transport.Attach legacyAttach = new org.apache.qpid.proton.amqp.transport.Attach();

        if (attach.hasName()) {
            legacyAttach.setName(attach.getName());
        }
        if (attach.hasHandle()) {
            legacyAttach.setHandle(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(attach.getHandle()));
        }
        if (attach.hasRole()) {
            legacyAttach.setRole(convertToLegacyType(attach.getRole()));
        }
        if (attach.hasSenderSettleMode()) {
            legacyAttach.setSndSettleMode(convertToLegacyType(attach.getSndSettleMode()));
        }
        if (attach.hasReceiverSettleMode()) {
            legacyAttach.setRcvSettleMode(convertToLegacyType(attach.getRcvSettleMode()));
        }
        if (attach.hasIncompleteUnsettled()) {
            legacyAttach.setIncompleteUnsettled(attach.getIncompleteUnsettled());
        }
        if (attach.hasOfferedCapabilites()) {
            legacyAttach.setOfferedCapabilities(convertToLegacyType(attach.getOfferedCapabilities()));
        }
        if (attach.hasDesiredCapabilites()) {
            legacyAttach.setDesiredCapabilities(convertToLegacyType(attach.getDesiredCapabilities()));
        }
        if (attach.hasProperties()) {
            legacyAttach.setProperties(convertToLegacyType(attach.getProperties()));
        }
        if (attach.hasInitialDeliveryCount()) {
            legacyAttach.setInitialDeliveryCount(org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(attach.getInitialDeliveryCount()));
        }
        if (attach.hasMaxMessageSize()) {
            legacyAttach.setMaxMessageSize(convertToLegacyType(attach.getMaxMessageSize()));
        }

        // TODO private Source source;
        // TODO private Target target;
        // TODO private Map<Binary, DeliveryState> unsettled;

        return legacyAttach;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param map
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static Map<?, ?> convertToLegacyType(Map<?, ?> map) {
        Map<Object, Object> legacySafeMap = new LinkedHashMap<>();

        for (Entry<?, ?> entry : map.entrySet()) {
            legacySafeMap.put(convertToLegacyType(entry.getKey()), convertToLegacyType(entry.getValue()));
        }

        return legacySafeMap;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param symbols
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Symbol[] convertToLegacyType(Symbol[] symbols) {
        org.apache.qpid.proton.amqp.Symbol[] legacySymbols = new org.apache.qpid.proton.amqp.Symbol[symbols.length];

        for (int i = 0; i < symbols.length; ++i) {
            legacySymbols[i] = org.apache.qpid.proton.amqp.Symbol.valueOf(symbols[i].toString());
        }

        return legacySymbols;
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param binary
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Binary convertToLegacyType(Binary binary) {
        byte[] copy = new byte[binary.getLength()];
        System.arraycopy(binary.getArray(), binary.getArrayOffset(), copy, 0, copy.length);
        return new org.apache.qpid.proton.amqp.Binary(copy);
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param symbol
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Symbol convertToLegacyType(Symbol symbol) {
        return org.apache.qpid.proton.amqp.Symbol.valueOf(symbol.toString());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param ubyte
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.UnsignedByte convertToLegacyType(UnsignedByte ubyte) {
        return org.apache.qpid.proton.amqp.UnsignedByte.valueOf(ubyte.byteValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param ushort
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.UnsignedShort convertToLegacyType(UnsignedShort ushort) {
        return org.apache.qpid.proton.amqp.UnsignedShort.valueOf(ushort.shortValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param uint
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.UnsignedInteger convertToLegacyType(UnsignedInteger uint) {
        return org.apache.qpid.proton.amqp.UnsignedInteger.valueOf(uint.intValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param ulong
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.UnsignedLong convertToLegacyType(UnsignedLong ulong) {
        return org.apache.qpid.proton.amqp.UnsignedLong.valueOf(ulong.longValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param decimal32
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Decimal32 convertToLegacyType(Decimal32 decimal32) {
        return new org.apache.qpid.proton.amqp.Decimal32(decimal32.intValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param decimal64
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Decimal64 convertToLegacyType(Decimal64 decimal64) {
        return new org.apache.qpid.proton.amqp.Decimal64(decimal64.longValue());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param decimal128
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.Decimal128 convertToLegacyType(Decimal128 decimal128) {
        return new org.apache.qpid.proton.amqp.Decimal128(decimal128.asBytes());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param terminusDurability
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.messaging.TerminusDurability convertToLegacyType(TerminusDurability terminusDurability) {
        return org.apache.qpid.proton.amqp.messaging.TerminusDurability.valueOf(terminusDurability.name());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param terminusExpiryPolicy
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.messaging.TerminusExpiryPolicy convertToLegacyType(TerminusExpiryPolicy terminusExpiryPolicy) {
        return org.apache.qpid.proton.amqp.messaging.TerminusExpiryPolicy.valueOf(terminusExpiryPolicy.name());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param role
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.Role convertToLegacyType(Role role) {
        return org.apache.qpid.proton.amqp.transport.Role.valueOf(role.name());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param senderSettleMode
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.SenderSettleMode convertToLegacyType(SenderSettleMode senderSettleMode) {
        return org.apache.qpid.proton.amqp.transport.SenderSettleMode.valueOf(senderSettleMode.name());
    }

    /**
     * convert a new Codec type to a legacy type for encoding or other operation that requires a legacy type.
     *
     * @param receiverSettleMode
     *      The new codec type to be converted to the legacy codec version
     *
     * @return the legacy version of the new type.
     */
    public static org.apache.qpid.proton.amqp.transport.ReceiverSettleMode convertToLegacyType(ReceiverSettleMode receiverSettleMode) {
        return org.apache.qpid.proton.amqp.transport.ReceiverSettleMode.valueOf(receiverSettleMode.name());
    }
}
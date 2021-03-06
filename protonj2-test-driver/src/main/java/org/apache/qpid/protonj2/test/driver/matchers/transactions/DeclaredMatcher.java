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
package org.apache.qpid.protonj2.test.driver.matchers.transactions;

import static org.hamcrest.CoreMatchers.equalTo;

import org.apache.qpid.protonj2.test.driver.codec.primitives.Binary;
import org.apache.qpid.protonj2.test.driver.codec.transactions.Declared;
import org.apache.qpid.protonj2.test.driver.matchers.ListDescribedTypeMatcher;
import org.hamcrest.Matcher;

public class DeclaredMatcher extends ListDescribedTypeMatcher {

    public DeclaredMatcher() {
        super(Declared.Field.values().length, Declared.DESCRIPTOR_CODE, Declared.DESCRIPTOR_SYMBOL);
    }

    @Override
    protected Class<?> getDescribedTypeClass() {
        return Declared.class;
    }

    //----- Type specific with methods that perform simple equals checks

    public DeclaredMatcher withTxnId(byte[] txnId) {
        return withTxnId(equalTo(new Binary(txnId)));
    }

    public DeclaredMatcher withTxnId(Binary txnId) {
        return withTxnId(equalTo(txnId));
    }

    //----- Matcher based with methods for more complex validation

    public DeclaredMatcher withTxnId(Matcher<?> m) {
        addFieldMatcher(Declared.Field.TXN_ID, m);
        return this;
    }
}

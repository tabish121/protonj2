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
package org.apache.qpid.protonj2.test.driver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * An in VM single threaded test driver used for testing Engine implementations
 * where all test operations will take place in a single thread of control.
 *
 * This class in mainly intended for use in JUnit tests of an Engine implementation
 * and not for use by client implementations where a socket based test peer would be
 * a more appropriate choice.
 */
public class ProtonTestPeer extends ScriptWriter implements Consumer<ByteBuffer>, AutoCloseable {

    private final AMQPTestDriver driver;
    private final Consumer<ByteBuffer> inputConsumer;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean rejecting = new AtomicBoolean();

    public ProtonTestPeer(Consumer<ByteBuffer> frameSink) {
        this.driver = new AMQPTestDriver((frame) -> {
            processDriverOutput(frame);
        }, null);

        this.inputConsumer = frameSink;
    }

    public int getEmptyFrameCount() {
        return driver.getEmptyFrameCount();
    }

    public int getPerformativeCount() {
        return driver.getPerformativeCount();
    }

    public int getSaslPerformativeCount() {
        return driver.getSaslPerformativeCount();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            processCloseRequest();
        }
    }

    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void accept(ByteBuffer frame) {
        if (rejecting.get()) {
            throw new UncheckedIOException("Driver is not accepting any new input", new IOException());
        } else {
            driver.accept(frame);
        }
    }

    //----- Test Completion API

    public void waitForScriptToCompleteIgnoreErrors() {
        driver.waitForScriptToCompleteIgnoreErrors();
    }

    public void waitForScriptToComplete() {
        driver.waitForScriptToComplete();
    }

    public void waitForScriptToComplete(long timeout) {
        driver.waitForScriptToComplete(timeout);
    }

    public void waitForScriptToComplete(long timeout, TimeUnit units) {
        driver.waitForScriptToComplete(timeout, units);
    }

    //----- Test scripting specific to this in VM test driver

    /**
     * After all scripted elements of the test are complete this will place the driver into
     * a mode where any new data is rejected with an exception.
     */
    public void rejectDataAfterLastScriptedElement() {
        driver.addScriptedElement(new ScriptedAction() {

            @Override
            public ScriptedAction queue() {
                return this;
            }

            @Override
            public ScriptedAction perform(AMQPTestDriver driver) {
                rejecting.set(true);
                return this;
            }

            @Override
            public ScriptedAction now() {
                return this;
            }

            @Override
            public ScriptedAction later(int waitTime) {
                return this;
            }
        });
    }

    //----- Internal implementation which can be overridden

    protected void processCloseRequest() {
        // nothing to do in this peer implementation.
    }

    protected void processDriverOutput(ByteBuffer frame) {
        inputConsumer.accept(frame);
    }

    @Override
    protected AMQPTestDriver getDriver() {
        return driver;
    }
}
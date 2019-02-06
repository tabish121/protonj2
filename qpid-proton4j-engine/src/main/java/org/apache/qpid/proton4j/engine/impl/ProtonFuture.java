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
package org.apache.qpid.proton4j.engine.impl;

import org.apache.qpid.proton4j.engine.AsyncResult;
import org.apache.qpid.proton4j.engine.EventHandler;

/**
 * Implementation of AsyncResult used by the proton4j engine for asynchronous
 * operations like Connection open etc.
 */
public class ProtonFuture<E> implements AsyncResult<E>, EventHandler<AsyncResult<E>> {

    private E result;
    private Throwable error;

    private EventHandler<AsyncResult<E>> handler;

    private boolean suceeded;
    private boolean failed;

    @Override
    public E get() {
        return result;
    }

    @Override
    public Throwable error() {
        return error;
    }

    @Override
    public boolean succeeded() {
        return suceeded;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public void handle(AsyncResult<E> target) {
        if (target.succeeded()) {
            onSuccess(target.get());
        } else {
            onFailure(target.error());
        }
    }

    ProtonFuture<E> setHandler(EventHandler<AsyncResult<E>> handler) {
      if (suceeded || failed) {
          handler.handle(this);
      } else {
          this.handler = handler;
      }

      return this;
    }

    void onSuccess(E result) {
        if (suceeded || failed) {
            return;
        }

        this.result = result;
        this.suceeded = true;

        if (handler != null) {
            handler.handle(this);
        }
    }

    void onFailure(Throwable error) {
        if (suceeded || failed) {
            return;
        }

        this.error = error;
        this.failed = true;

        if (handler != null) {
            handler.handle(this);
        }
    }
}
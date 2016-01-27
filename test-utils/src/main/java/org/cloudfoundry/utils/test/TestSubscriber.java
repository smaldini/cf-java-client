/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.utils.test;

import org.atteo.evo.inflector.English;
import org.junit.Assert;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.util.Exceptions;
import reactor.core.util.ReactiveStateUtils;
import reactor.fn.Consumer;
import reactor.fn.Supplier;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public final class TestSubscriber<T> implements Subscriber<T> {

    private final Queue<T> actuals = new LinkedList<T>();

    private final Queue<Consumer<T>> expectations = new LinkedList<>();

    private final CountDownLatch latch = new CountDownLatch(1);

    private Integer countExpectation;

    private volatile Throwable errorActual;

    private Consumer<? super Throwable> errorExpectation;

    private Consumer<Tuple2<Long, Long>> performanceCallback;

    private Consumer<Subscription> scanningCallback;

    private long startTime;

    private Subscription subscription;

    public TestSubscriber<T> assertCount(Integer expected) {
        this.countExpectation = expected;
        return this;
    }

    public TestSubscriber<T> assertEquals(final T expected) {
        assertThat(new Consumer<T>() {

            @Override
            public void accept(T actual) {
                Assert.assertEquals(expected, actual);
            }

        });

        return this;
    }

    public TestSubscriber<T> assertError(final Class<? extends Throwable> expected) {
        this.errorExpectation = new Consumer<Throwable>() {

            @Override
            public void accept(Throwable actual) {
                Assert.assertTrue(String.format("Unexpected error %s", actual),
                    expected.isAssignableFrom(actual.getClass()));
            }

        };

        return this;
    }

    public TestSubscriber<T> assertThat(Consumer<T> expectation) {
        this.expectations.add(expectation);
        return this;
    }

    @Override
    public void onComplete() {
        this.latch.countDown();
    }

    @Override
    public void onError(Throwable t) {
        Exceptions.throwIfFatal(t);
        this.errorActual = t;
        this.latch.countDown();
    }

    @Override
    public void onNext(T t) {
        this.actuals.add(t);

        if (this.scanningCallback != null) {
            this.scanningCallback.accept(this.subscription);
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        this.startTime = System.currentTimeMillis();

        s.request(Long.MAX_VALUE);
    }

    public TestSubscriber<T> setPerformanceCallback(Consumer<Tuple2<Long, Long>> performanceCallback) {
        this.performanceCallback = performanceCallback;
        return this;
    }

    public TestSubscriber<T> setPerformanceLoggerName(final Supplier<String> name) {
        return setPerformanceCallback(new Consumer<Tuple2<Long, Long>>() {

            @Override
            public void accept(Tuple2<Long, Long> tuple) {
                Long startTime = tuple.t1;
                Long finishTime = tuple.t2;

                Logger logger = LoggerFactory.getLogger(String.format("performance.%s", name.get()));
                if (logger.isDebugEnabled()) {
                    logger.debug("{} ms", finishTime - startTime);
                }
            }

        });
    }

    public TestSubscriber<T> setScanningCallback(Consumer<Subscription> scanningCallback) {
        this.scanningCallback = scanningCallback;
        return this;
    }

    public TestSubscriber<T> setScanningLoggerName(final Supplier<String> name) {
        return setScanningCallback(new Consumer<Subscription>() {

            @Override
            public void accept(Subscription subscription) {
                Logger logger = LoggerFactory.getLogger(String.format("scan.%s", name.get()));

                if (logger.isDebugEnabled()) {
                    logger.debug(ReactiveStateUtils.scan(subscription).toString());
                }
            }

        });
    }

    public void verify(long timeout, TimeUnit unit) throws InterruptedException {
        if (!this.latch.await(timeout, unit)) {
            throw new IllegalStateException("Subscriber timed out");
        }

        if (this.performanceCallback != null) {
            this.performanceCallback.accept(Tuple.of(this.startTime, System.currentTimeMillis()));
        }

        verifyError();
        verifyCount();
        verifyItems();
    }

    private void verifyCount() {
        if (this.countExpectation != null) {
            Assert.assertEquals("Item count expectation not met", this.countExpectation, (Integer) this.actuals.size());
        }
    }

    private void verifyError() {
        if (this.errorActual != null) {
            if (this.errorExpectation == null) {
                throw new AssertionError("Unexpected error", this.errorActual);
            }

            this.errorExpectation.accept(this.errorActual);
            this.errorExpectation = null;
        }

        if (this.errorExpectation != null) {
            fail("Unexpected completion. Error expectation not met.");
        }
    }

    private void verifyItems() {
        for (T actual : this.actuals) {
            Consumer<T> expectation = this.expectations.poll();

            if (expectation != null) {
                expectation.accept(actual);
            } else if (this.countExpectation == null) {
                fail(String.format("Unexpected item %s", actual));
            }
        }

        if (!this.expectations.isEmpty()) {
            int count = this.expectations.size();
            fail(String.format("Unexpected completion. %d %s not met.", count, English.plural("expectation", count)));
        }
    }

}

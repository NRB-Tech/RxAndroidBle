package io.nrbtech.rxandroidble.internal.util;

import io.nrbtech.rxandroidble.internal.operations.TimeoutConfiguration;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Scheduler;


public class MockOperationTimeoutConfiguration extends TimeoutConfiguration {

    public static final int TIMEOUT_IN_SEC = 30;

    public MockOperationTimeoutConfiguration(int seconds, Scheduler timeoutScheduler) {
        super(seconds, TimeUnit.SECONDS, timeoutScheduler);
    }

    public MockOperationTimeoutConfiguration(Scheduler timeoutScheduler) {
        this(TIMEOUT_IN_SEC, timeoutScheduler);
    }
}

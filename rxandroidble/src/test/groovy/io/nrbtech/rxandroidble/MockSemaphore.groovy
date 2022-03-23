package io.nrbtech.rxandroidble

import io.nrbtech.rxandroidble.internal.serialization.QueueAwaitReleaseInterface
import io.nrbtech.rxandroidble.internal.serialization.QueueReleaseInterface

class MockSemaphore implements QueueReleaseInterface, QueueAwaitReleaseInterface {
    int permits = 0

    MockSemaphore() {
    }

    @Override
    void awaitRelease() throws InterruptedException {
        permits++
    }

    @Override
    void release() {
        permits--
    }

    boolean isReleased() {
        permits <= 0
    }
}

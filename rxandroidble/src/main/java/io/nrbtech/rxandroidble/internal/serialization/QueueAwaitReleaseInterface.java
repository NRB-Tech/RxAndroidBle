package io.nrbtech.rxandroidble.internal.serialization;


/**
 * Interface used for blocking the Operation Queue before executing next command.
 *
 * @see QueueReleaseInterface
 * @see QueueSemaphore
 * @see ClientOperationQueue
 * @see ConnectionOperationQueue
 */
public interface QueueAwaitReleaseInterface {

    void awaitRelease() throws InterruptedException;
}

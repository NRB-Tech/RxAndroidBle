package io.nrbtech.rxandroidble.internal.connection;


/**
 * Interface for all classes that should be called when the user subscribes to/disposes
 * {@link io.nrbtech.rxandroidble.RxBleDevice#establishConnection(boolean)}
 *
 * The binding which injects the interface to a {@link ConnectorImpl} is in {@link ConnectionModule}
 */
public interface ConnectionSubscriptionWatcher {

    /**
     * Method to be called when the user subscribes to an individual
     * {@link io.nrbtech.rxandroidble.RxBleDevice#establishConnection(boolean)}
     */
    void onConnectionSubscribed();

    /**
     * Method to be called when the user disposes an individual
     * {@link io.nrbtech.rxandroidble.RxBleDevice#establishConnection(boolean)}
     */
    void onConnectionUnsubscribed();
}

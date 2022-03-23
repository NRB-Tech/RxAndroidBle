package io.nrbtech.rxandroidble.internal.connection;


import io.nrbtech.rxandroidble.RxBleConnection;

public interface ConnectionStateChangeListener {

    void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState);
}

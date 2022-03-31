package io.nrbtech.rxandroidble.internal.connection;


import android.bluetooth.BluetoothGattDescriptor;

import io.nrbtech.rxandroidble.internal.operations.OperationsProvider;
import io.nrbtech.rxandroidble.internal.serialization.ConnectionOperationQueue;

import bleshadow.javax.inject.Inject;
import io.reactivex.rxjava3.core.Completable;

@ConnectionScope
class DescriptorWriter {

    private final ConnectionOperationQueue operationQueue;
    private final OperationsProvider operationsProvider;

    @Inject
    DescriptorWriter(ConnectionOperationQueue operationQueue, OperationsProvider operationsProvider) {
        this.operationQueue = operationQueue;
        this.operationsProvider = operationsProvider;
    }

    Completable writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        return operationQueue.queue(operationsProvider.provideWriteDescriptor(bluetoothGattDescriptor, data))
                .ignoreElements();
    }
}

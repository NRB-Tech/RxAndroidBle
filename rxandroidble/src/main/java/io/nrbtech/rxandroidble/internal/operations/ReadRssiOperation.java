package io.nrbtech.rxandroidble.internal.operations;

import android.bluetooth.BluetoothGatt;

import io.nrbtech.rxandroidble.exceptions.BleGattOperationType;
import io.nrbtech.rxandroidble.internal.SingleResponseOperation;
import io.nrbtech.rxandroidble.internal.connection.ConnectionModule;
import io.nrbtech.rxandroidble.internal.connection.RxBleGattCallback;

import bleshadow.javax.inject.Inject;
import bleshadow.javax.inject.Named;

import io.reactivex.Single;

public class ReadRssiOperation extends SingleResponseOperation<Integer> {

    @Inject
    ReadRssiOperation(RxBleGattCallback bleGattCallback, BluetoothGatt bluetoothGatt,
                      @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration) {
        super(bluetoothGatt, bleGattCallback, BleGattOperationType.READ_RSSI, timeoutConfiguration);
    }

    @Override
    protected Single<Integer> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback.getOnRssiRead().firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readRemoteRssi();
    }

    @Override
    public String toString() {
        return "ReadRssiOperation{" + super.toString() + '}';
    }
}

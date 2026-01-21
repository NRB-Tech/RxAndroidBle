package io.nrbtech.rxandroidble.internal.operations;

import android.bluetooth.BluetoothGatt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import io.nrbtech.rxandroidble.PhyPair;
import io.nrbtech.rxandroidble.exceptions.BleGattOperationType;
import io.nrbtech.rxandroidble.internal.SingleResponseOperation;
import io.nrbtech.rxandroidble.internal.connection.ConnectionModule;
import io.nrbtech.rxandroidble.internal.connection.RxBleGattCallback;

import bleshadow.javax.inject.Inject;
import bleshadow.javax.inject.Named;
import io.reactivex.rxjava3.core.Single;

@RequiresApi(26 /* Build.VERSION_CODES.O */)
public class PhyReadOperation extends SingleResponseOperation<PhyPair> {

    @Inject
    PhyReadOperation(RxBleGattCallback bleGattCallback, BluetoothGatt bluetoothGatt,
                     @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration) {
        super(bluetoothGatt, bleGattCallback, BleGattOperationType.PHY_READ, timeoutConfiguration);
    }

    @Override
    protected Single<PhyPair> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback.getOnPhyRead().firstOrError();
    }

    @Override
    @RequiresPermission("android.permission.BLUETOOTH_CONNECT")
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        bluetoothGatt.readPhy();
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhyReadOperation{" + super.toString() + '}';
    }
}

package io.nrbtech.rxandroidble.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import io.nrbtech.rxandroidble.exceptions.BleGattOperationType;
import io.nrbtech.rxandroidble.internal.SingleResponseOperation;
import io.nrbtech.rxandroidble.internal.connection.ConnectionModule;
import io.nrbtech.rxandroidble.internal.connection.RxBleGattCallback;
import io.nrbtech.rxandroidble.internal.logger.LoggerUtil;
import io.nrbtech.rxandroidble.internal.util.ByteAssociation;

import bleshadow.javax.inject.Inject;
import bleshadow.javax.inject.Named;

import io.reactivex.rxjava3.core.Single;

import static io.nrbtech.rxandroidble.internal.util.ByteAssociationUtil.descriptorPredicate;

public class DescriptorReadOperation extends SingleResponseOperation<ByteAssociation<BluetoothGattDescriptor>> {

    private final BluetoothGattDescriptor bluetoothGattDescriptor;

    @Inject
    DescriptorReadOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                            @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                            BluetoothGattDescriptor descriptor) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.DESCRIPTOR_READ, timeoutConfiguration);
        bluetoothGattDescriptor = descriptor;
    }

    @Override
    protected Single<ByteAssociation<BluetoothGattDescriptor>> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnDescriptorRead()
                .filter(descriptorPredicate(bluetoothGattDescriptor))
                .firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readDescriptor(bluetoothGattDescriptor);
    }

    @Override
    public String toString() {
        return "DescriptorReadOperation{"
                + super.toString()
                + ", descriptor=" + LoggerUtil.wrap(bluetoothGattDescriptor, false)
                + '}';
    }
}

package io.nrbtech.rxandroidble.internal.connection;


import android.bluetooth.BluetoothGattCharacteristic;
import androidx.annotation.Nullable;

import io.nrbtech.rxandroidble.internal.BluetoothGattCharacteristicProperty;
import io.nrbtech.rxandroidble.internal.BleIllegalOperationException;

/**
 * Handler for {@link IllegalOperationChecker#checkAnyPropertyMatches(BluetoothGattCharacteristic, int)} response.
 */
public abstract class IllegalOperationHandler {

    protected final IllegalOperationMessageCreator messageCreator;

    IllegalOperationHandler(IllegalOperationMessageCreator messageCreator) {
        this.messageCreator = messageCreator;
    }

    public abstract @Nullable BleIllegalOperationException handleMismatchData(BluetoothGattCharacteristic characteristic,
                                                                              @BluetoothGattCharacteristicProperty int neededProperties);
}

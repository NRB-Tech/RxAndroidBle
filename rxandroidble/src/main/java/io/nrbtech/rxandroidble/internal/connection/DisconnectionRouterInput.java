package io.nrbtech.rxandroidble.internal.connection;


import android.bluetooth.BluetoothGatt;
import io.nrbtech.rxandroidble.exceptions.BleDisconnectedException;
import io.nrbtech.rxandroidble.exceptions.BleGattException;

/**
 * Interface for routing causes of the disconnection. This may be for instance errors caused by
 * {@link android.bluetooth.BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)}
 */
interface DisconnectionRouterInput {

    /**
     * Method to be called whenever a connection braking exception happens.
     *
     * @param disconnectedException the exception that happened
     */
    void onDisconnectedException(BleDisconnectedException disconnectedException);

    /**
     * Method to be called whenever a BluetoothGattCallback.onConnectionStateChange() will get called with status != GATT_SUCCESS
     *
     * @param disconnectedGattException the exception that happened
     */
    void onGattConnectionStateException(BleGattException disconnectedGattException);
}

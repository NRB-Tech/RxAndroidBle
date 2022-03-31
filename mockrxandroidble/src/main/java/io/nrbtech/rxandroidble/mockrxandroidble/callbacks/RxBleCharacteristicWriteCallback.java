package io.nrbtech.rxandroidble.mockrxandroidble.callbacks;

import android.bluetooth.BluetoothGattCharacteristic;

import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.results.RxBleGattWriteResultMock;
import io.nrbtech.rxandroidble.mockrxandroidble.RxBleDeviceMock;

/**
 * An interface for a user callback for handling characteristic write requests
 */
public interface RxBleCharacteristicWriteCallback {

    /**
     * Handles a write on a GATT characteristic
     * @param device the device being written to
     * @param characteristic the characteristic being written to
     * @param data the data being written
     * @param result the result handler
     * @throws Exception on error
     */
    void handle(RxBleDeviceMock device,
                BluetoothGattCharacteristic characteristic,
                byte[] data,
                RxBleGattWriteResultMock result) throws Exception;
}

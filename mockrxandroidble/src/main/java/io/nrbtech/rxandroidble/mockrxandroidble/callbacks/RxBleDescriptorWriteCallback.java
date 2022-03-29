package io.nrbtech.rxandroidble.mockrxandroidble.callbacks;

import android.bluetooth.BluetoothGattDescriptor;

import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.results.RxBleGattWriteResultMock;
import io.nrbtech.rxandroidble.mockrxandroidble.RxBleDeviceMock;

/**
 * An interface for a user callback for handling descriptor write requests
 */
public interface RxBleDescriptorWriteCallback {

    /**
     * Handles a write on a GATT descriptor
     * @param device the device being written to
     * @param descriptor the descriptor being written to
     * @param data the data being written
     * @param result the result handler
     * @throws Exception on error
     */
    void handle(RxBleDeviceMock device, BluetoothGattDescriptor descriptor, byte[] data, RxBleGattWriteResultMock result) throws Exception;
}

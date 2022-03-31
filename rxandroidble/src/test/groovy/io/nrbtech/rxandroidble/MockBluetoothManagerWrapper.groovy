package io.nrbtech.rxandroidble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import io.nrbtech.rxandroidble.internal.util.BluetoothManagerWrapper

class MockBluetoothManagerWrapper extends BluetoothManagerWrapper {

    private List<BluetoothDevice> connectedPeripherals = new ArrayList<>()

    MockBluetoothManagerWrapper() {
        super(null)
    }

    MockBluetoothManagerWrapper(BluetoothManager bluetoothManager) {
        super(bluetoothManager)
    }

    def addConnectedPeripheral(BluetoothDevice bluetoothDevice) {
        connectedPeripherals.add(bluetoothDevice)
    }

    @Override
    List<BluetoothDevice> getConnectedPeripherals() {
        return connectedPeripherals
    }
}

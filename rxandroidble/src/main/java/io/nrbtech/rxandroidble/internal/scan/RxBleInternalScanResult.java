package io.nrbtech.rxandroidble.internal.scan;

import android.bluetooth.BluetoothDevice;

import io.nrbtech.rxandroidble.internal.ScanResultInterface;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;

public class RxBleInternalScanResult implements ScanResultInterface {

    private final BluetoothDevice bluetoothDevice;
    private final int rssi;
    private final long timestampNanos;
    private final ScanRecord scanRecord;
    private final ScanCallbackType scanCallbackType;
    private final Boolean isConnectable;

    public RxBleInternalScanResult(BluetoothDevice bluetoothDevice, int rssi, long timestampNanos, ScanRecord scanRecord,
                                   ScanCallbackType scanCallbackType, Boolean isConnectable) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.timestampNanos = timestampNanos;
        this.scanRecord = scanRecord;
        this.scanCallbackType = scanCallbackType;
        this.isConnectable = isConnectable;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    @Override
    public long getTimestampNanos() {
        return timestampNanos;
    }

    @Override
    public ScanCallbackType getScanCallbackType() {
        return scanCallbackType;
    }

    public Boolean isConnectable() {
        return isConnectable;
    }

    @Override
    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    @Override
    public String getDeviceName() {
        BluetoothDevice device = getBluetoothDevice();
        return device == null ? null : device.getName();
    }
}

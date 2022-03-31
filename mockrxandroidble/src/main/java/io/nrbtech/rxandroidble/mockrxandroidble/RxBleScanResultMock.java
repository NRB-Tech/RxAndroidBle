package io.nrbtech.rxandroidble.mockrxandroidble;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.internal.ScanResultInterface;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;
import io.nrbtech.rxandroidble.scan.ScanResult;

public class RxBleScanResultMock extends ScanResult implements ScanResultInterface {
    public RxBleScanResultMock(RxBleDevice bleDevice,
                               int rssi,
                               long timestampNanos,
                               ScanCallbackType callbackType,
                               ScanRecord scanRecord,
                               IsConnectableStatus isConnectable) {
        super(bleDevice, rssi, timestampNanos, callbackType, scanRecord, isConnectable);
    }

    public String getAddress() {
        RxBleDevice device = getBleDevice();
        return device == null ? null : device.getMacAddress();
    }

    public String getDeviceName() {
        RxBleDevice device = getBleDevice();
        return device == null ? null : device.getName();
    }

    public ScanCallbackType getScanCallbackType() {
        return getCallbackType();
    }
}

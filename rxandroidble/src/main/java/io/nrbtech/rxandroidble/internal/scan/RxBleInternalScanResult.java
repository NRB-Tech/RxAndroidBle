package io.nrbtech.rxandroidble.internal.scan;

import android.bluetooth.BluetoothDevice;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.scan.ScanResultInterface;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;

public class RxBleInternalScanResult implements ScanResultInterface {

    private final BluetoothDevice bluetoothDevice;
    // Android O or later, represent as nullable properties
    private final Boolean isLegacy;
    private final Boolean isConnectable;
    private final DataStatus dataStatus;
    private final RxBleDevice.Phy primaryPhy;
    private final RxBleDevice.Phy secondaryPhy;
    private final Integer advertisingSid;
    private final Integer txPower;
    private final int rssi;
    private final Integer periodicAdvertisingInterval;
    private final ScanRecord scanRecord;
    private final long timestampNanos;
    private final ScanCallbackType scanCallbackType;

    public RxBleInternalScanResult(BluetoothDevice bluetoothDevice, Boolean isLegacy, Boolean isConnectableStatus,
                                   DataStatus dataStatus, RxBleDevice.Phy primaryPhy, RxBleDevice.Phy secondaryPhy,
                                   Integer advertisingSid, Integer txPower, int rssi, Integer periodicAdvertisingInterval,
                                   ScanRecord scanRecord, long timestampNanos, ScanCallbackType scanCallbackType) {
        this.bluetoothDevice = bluetoothDevice;
        this.isLegacy = isLegacy;
        this.isConnectable = isConnectableStatus;
        this.dataStatus = dataStatus;
        this.primaryPhy = primaryPhy;
        this.secondaryPhy = secondaryPhy;
        this.advertisingSid = advertisingSid;
        this.txPower = txPower;
        this.rssi = rssi;
        this.periodicAdvertisingInterval = periodicAdvertisingInterval;
        this.scanRecord = scanRecord;
        this.timestampNanos = timestampNanos;
        this.scanCallbackType = scanCallbackType;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public String getAddress() {
        BluetoothDevice device = getBluetoothDevice();
        return device == null ? null : device.getAddress();
    }

    @Override
    public String getDeviceName() {
        BluetoothDevice device = getBluetoothDevice();
        return device == null ? null : device.getName();
    }

    public int getRssi() {
        return rssi;
    }

    public long getTimestampNanos() {
        return timestampNanos;
    }

    public ScanCallbackType getScanCallbackType() {
        return scanCallbackType;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    @Override
    public Boolean isLegacy() {
        return isLegacy;
    }

    @Override
    public DataStatus getDataStatus() {
        return dataStatus;
    }

    @Override
    public Boolean isConnectable() {
        return isConnectable;
    }

    @Override
    public RxBleDevice.Phy getPrimaryPhy() {
        return primaryPhy;
    }

    @Override
    public RxBleDevice.Phy getSecondaryPhy() {
        return secondaryPhy;
    }

    @Override
    public Integer getAdvertisingSid() {
        return advertisingSid;
    }

    @Override
    public Integer getTxPower() {
        return txPower;
    }

    @Override
    public Integer getPeriodicAdvertisingInterval() {
        return periodicAdvertisingInterval;
    }
}

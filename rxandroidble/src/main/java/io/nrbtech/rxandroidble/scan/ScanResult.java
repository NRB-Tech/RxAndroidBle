package io.nrbtech.rxandroidble.scan;

import androidx.annotation.NonNull;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.internal.logger.LoggerUtil;

public class ScanResult implements ScanResultInterface {

    private final RxBleDevice bleDevice;
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

    /**
     * Constructs a new ScanResult.
     *
     * @param bleDevice Remote RxBleDevice found.
     * @param primaryPhy Primary advertising phy.
     * @param secondaryPhy Secondary advertising phy.
     * @param advertisingSid Advertising set ID.
     * @param txPower Transmit power.
     * @param rssi Received signal strength.
     * @param periodicAdvertisingInterval Periodic advertising interval.
     * @param scanRecord Scan record including both advertising data and scan response data.
     * @param timestampNanos Timestamp at which the scan result was observed.
     * @param scanCallbackType The scan callback type
     */
    public ScanResult(RxBleDevice bleDevice, Boolean isLegacy, Boolean isConnectableStatus, DataStatus dataStatus,
                      RxBleDevice.Phy primaryPhy, RxBleDevice.Phy secondaryPhy, Integer advertisingSid, Integer txPower, int rssi,
                      Integer periodicAdvertisingInterval, ScanRecord scanRecord, long timestampNanos, ScanCallbackType scanCallbackType) {
        this.bleDevice = bleDevice;
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

    public RxBleDevice getBleDevice() {
        return bleDevice;
    }

    @Override
    public String getAddress() {
        RxBleDevice device = getBleDevice();
        return device == null ? null : device.getMacAddress();
    }

    @Override
    public String getDeviceName() {
        RxBleDevice device = getBleDevice();
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

    public Boolean isLegacy() {
        return isLegacy;
    }

    public Boolean isConnectable() {
        return isConnectable;
    }

    public DataStatus getDataStatus() {
        // return bit 5 and 6
        return dataStatus;
    }

    public RxBleDevice.Phy getPrimaryPhy() {
        return primaryPhy;
    }

    public RxBleDevice.Phy getSecondaryPhy() {
        return secondaryPhy;
    }

    public Integer getAdvertisingSid() {
        return advertisingSid;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public Integer getPeriodicAdvertisingInterval() {
        return periodicAdvertisingInterval;
    }

    @Override
    @NonNull
    public String toString() {
        return "ScanResult{"
                + "bleDevice=" + bleDevice
                + ", rssi=" + rssi
                + ", timestampNanos=" + timestampNanos
                + ", callbackType=" + scanCallbackType
                + ", scanRecord=" + LoggerUtil.bytesToHex(scanRecord.getBytes())
                + ", isLegacy=" + isLegacy()
                + ", isConnectable=" + isConnectable()
                + ", DataStatus=" + getDataStatus()
                + ", PrimaryPhy=" + getPrimaryPhy()
                + ", SecondaryPhy=" + getSecondaryPhy()
                + ", AdvertisingSid=" + getAdvertisingSid()
                + ", TxPower=" + getTxPower()
                + ", periodicAdvertisingInterval=" + getPeriodicAdvertisingInterval()
                + '}';
    }
}

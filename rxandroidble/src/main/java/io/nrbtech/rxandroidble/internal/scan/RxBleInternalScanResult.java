package io.nrbtech.rxandroidble.internal.scan;

import android.bluetooth.BluetoothDevice;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.internal.ScanResultInterface;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;
import io.nrbtech.rxandroidble.scan.ScanResult;

public class RxBleInternalScanResult implements ScanResultInterface {

    private final BluetoothDevice bluetoothDevice;
    // Android O or later, represent as nullable properties
    private final Boolean isLegacy;
    private final Boolean isConnectable;
    private final Integer dataStatus;
    private final Integer primaryPhy;
    private final Integer secondaryPhy;
    private final Integer advertisingSid;
    private final Integer txPower;
    private final int rssi;
    private final Integer periodicAdvertisingInterval;
    private final ScanRecord scanRecord;
    private final long timestampNanos;
    private final ScanCallbackType scanCallbackType;

    public RxBleInternalScanResult(BluetoothDevice bluetoothDevice, Boolean isLegacy, Boolean isConnectableStatus,
                                   Integer dataStatus, Integer primaryPhy, Integer secondaryPhy, Integer advertisingSid, Integer txPower,
                                   int rssi, Integer periodicAdvertisingInterval, ScanRecord scanRecord, long timestampNanos,
                                   ScanCallbackType scanCallbackType) {
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

    /**
     * Returns the received signal strength in dBm. The valid range is [-127, 126].
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Returns timestamp since boot when the scan record was observed.
     */
    public long getTimestampNanos() {
        return timestampNanos;
    }

    public ScanCallbackType getScanCallbackType() {
        return scanCallbackType;
    }

    /**
     * Returns the scan record, which is a combination of advertisement and scan response.
     */
    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    @Override
    public Boolean isLegacy() {
        return isLegacy;
    }

    @Override
    public Integer getDataStatus() {
        return dataStatus;
    }

    @Override
    public Boolean isConnectable() {
        return isConnectable;
    }

    /**
     * Returns the primary Physical Layer
     * on which this advertisement was received.
     * Can be one of {@link RxBleDevice#PHY_LE_1M} or
     * {@link RxBleDevice#PHY_LE_CODED}.
     */
    @Override
    public Integer getPrimaryPhy() {
        return primaryPhy;
    }

    /**
     * Returns the secondary Physical Layer
     * on which this advertisment was received.
     * Can be one of {@link RxBleDevice#PHY_LE_1M},
     * {@link RxBleDevice#PHY_LE_2M}, {@link RxBleDevice#PHY_LE_CODED}
     * or {@link ScanResult#PHY_UNUSED} - if the advertisement
     * was not received on a secondary physical channel.
     */
    @Override
    public Integer getSecondaryPhy() {
        return secondaryPhy;
    }

    /**
     * Returns the advertising set id.
     * May return {@link ScanResult#SID_NOT_PRESENT} if
     * no set id was is present.
     */
    @Override
    public Integer getAdvertisingSid() {
        return advertisingSid;
    }

    /**
     * Returns the transmit power in dBm.
     * Valid range is [-127, 126]. A value of {@link ScanResult#TX_POWER_NOT_PRESENT}
     * indicates that the TX power is not present.
     */
    @Override
    public Integer getTxPower() {
        return txPower;
    }

    /**
     * Returns the periodic advertising interval in units of 1.25ms.
     * Valid range is 6 (7.5ms) to 65536 (81918.75ms). A value of
     * {@link ScanResult#PERIODIC_INTERVAL_NOT_PRESENT} means periodic
     * advertising interval is not present.
     */
    @Override
    public Integer getPeriodicAdvertisingInterval() {
        return periodicAdvertisingInterval;
    }
}

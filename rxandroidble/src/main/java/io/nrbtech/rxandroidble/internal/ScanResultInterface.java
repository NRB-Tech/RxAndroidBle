package io.nrbtech.rxandroidble.internal;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;
import io.nrbtech.rxandroidble.scan.ScanResult;

public interface ScanResultInterface {
    /**
     * Get the address from the device
     */
    String getAddress();

    /**
     * Get the device name from the device (not from scan record)
     */
    String getDeviceName();

    /**
     * Returns the received signal strength in dBm. The valid range is [-127, 126].
     */
    int getRssi();

    /**
     * Returns the scan record, which is a combination of advertisement and scan response.
     */
    ScanRecord getScanRecord();

    /**
     * Returns timestamp since boot when the scan record was observed.
     */
    long getTimestampNanos();

    /**
     * Get the type of scan callback
     */
    ScanCallbackType getScanCallbackType();

    /**
     * Returns true if this object represents legacy scan result.
     * Legacy scan results do not contain advanced advertising information
     * as specified in the Bluetooth Core Specification v5.
     * If null, property could not be determined (API <26)
     */
    Boolean isLegacy();

    /**
     * Determine if the BLE device is connectable.
     * If null, property could not be determined (API <26)
     */
    Boolean isConnectable();

    /**
     * Returns the data status.
     * Can be one of {@link ScanResult#DATA_COMPLETE} or
     * {@link ScanResult#DATA_TRUNCATED}.
     * If null, property could not be determined (API <26)
     */
    Integer getDataStatus();

    /**
     * Returns the primary Physical Layer
     * on which this advertisement was received.
     * Can be one of {@link RxBleDevice#PHY_LE_1M} or
     * {@link RxBleDevice#PHY_LE_CODED}.
     * If null, property could not be determined (API <26)
     */
    Integer getPrimaryPhy();

    /**
     * Returns the secondary Physical Layer
     * on which this advertisment was received.
     * Can be one of {@link RxBleDevice#PHY_LE_1M},
     * {@link RxBleDevice#PHY_LE_2M}, {@link RxBleDevice#PHY_LE_CODED}
     * or {@link ScanResult#PHY_UNUSED} - if the advertisement
     * was not received on a secondary physical channel.
     * If null, property could not be determined (API <26)
     */
    Integer getSecondaryPhy();

    /**
     * Returns the advertising set id.
     * May return {@link ScanResult#SID_NOT_PRESENT} if
     * no set id was is present.
     * If null, property could not be determined (API <26)
     */
    Integer getAdvertisingSid();

    /**
     * Returns the transmit power in dBm.
     * Valid range is [-127, 126]. A value of {@link ScanResult#TX_POWER_NOT_PRESENT}
     * indicates that the TX power is not present.
     * If null, property could not be determined (API <26)
     */
    Integer getTxPower();

    /**
     * Returns the periodic advertising interval in units of 1.25ms.
     * Valid range is 6 (7.5ms) to 65536 (81918.75ms). A value of
     * {@link ScanResult#PERIODIC_INTERVAL_NOT_PRESENT} means periodic
     * advertising interval is not present.
     * If null, property could not be determined (API <26)
     */
    Integer getPeriodicAdvertisingInterval();
}

package io.nrbtech.rxandroidble.scan;

import io.nrbtech.rxandroidble.RxBleDevice;

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

    enum DataStatus {
        DataComplete,
        DataTruncated
    }

    /**
     * Returns the data status.
     * Can be one of {@link DataStatus#DataComplete} or
     * {@link DataStatus#DataTruncated}.
     * If null, property could not be determined (API <26)
     */
    DataStatus getDataStatus();

    /**
     * Returns the primary Physical Layer
     * on which this advertisement was received.
     * Can be one of {@link RxBleDevice.Phy#LE1M} or
     * {@link RxBleDevice.Phy#LE2M}.
     * If null, property could not be determined (API <26)
     */
    RxBleDevice.Phy getPrimaryPhy();

    /**
     * Returns the secondary Physical Layer
     * on which this advertisment was received.
     * Can be one of {@link RxBleDevice.Phy#LE1M},
     * {@link RxBleDevice.Phy#LE2M}, {@link RxBleDevice.Phy#LECoded}
     * or {@link RxBleDevice.Phy#Unused} - if the advertisement
     * was not received on a secondary physical channel.
     * If null, property could not be determined (API <26)
     */
    RxBleDevice.Phy getSecondaryPhy();

    /**
     * Advertising Set ID is not present in the packet.
     */
    int SID_NOT_PRESENT = 0xFF;

    /**
     * Returns the advertising set id.
     * May return {@link ScanResultInterface#SID_NOT_PRESENT} if
     * no set id was is present.
     * If null, property could not be determined (API <26)
     */
    Integer getAdvertisingSid();

    /**
     * TX power is not present in the packet.
     */
    int TX_POWER_NOT_PRESENT = 0x7F;

    /**
     * Returns the transmit power in dBm.
     * Valid range is [-127, 126]. A value of {@link ScanResultInterface#TX_POWER_NOT_PRESENT}
     * indicates that the TX power is not present.
     * If null, property could not be determined (API <26)
     */
    Integer getTxPower();

    /**
     * Periodic advertising interval is not present in the packet.
     */
    int PERIODIC_INTERVAL_NOT_PRESENT = 0x00;

    /**
     * Returns the periodic advertising interval in units of 1.25ms.
     * Valid range is 6 (7.5ms) to 65536 (81918.75ms). A value of
     * {@link ScanResultInterface#PERIODIC_INTERVAL_NOT_PRESENT} means periodic
     * advertising interval is not present.
     * If null, property could not be determined (API <26)
     */
    Integer getPeriodicAdvertisingInterval();
}

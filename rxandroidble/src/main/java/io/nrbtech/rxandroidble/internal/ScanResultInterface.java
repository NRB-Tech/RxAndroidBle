package io.nrbtech.rxandroidble.internal;

import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;

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
     * Get the RSSI of the scan result
     */
    int getRssi();

    /**
     * Get the scan record
     */
    ScanRecord getScanRecord();

    /**
     * Get the timestamp the scan result was produced
     */
    long getTimestampNanos();

    /**
     * Get the type of scan callback
     */
    ScanCallbackType getScanCallbackType();

    enum IsConnectableStatus {
        LEGACY_UNKNOWN,
        NOT_CONNECTABLE,
        CONNECTABLE
    }

    IsConnectableStatus isConnectable();
}

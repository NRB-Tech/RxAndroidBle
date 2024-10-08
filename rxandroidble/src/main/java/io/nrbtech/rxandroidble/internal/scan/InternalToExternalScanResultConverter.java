package io.nrbtech.rxandroidble.internal.scan;


import androidx.annotation.RestrictTo;

import io.nrbtech.rxandroidble.internal.RxBleDeviceProvider;
import io.nrbtech.rxandroidble.scan.ScanResult;

import bleshadow.javax.inject.Inject;

import io.reactivex.rxjava3.functions.Function;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class InternalToExternalScanResultConverter implements Function<RxBleInternalScanResult, ScanResult> {

    private final RxBleDeviceProvider deviceProvider;

    @Inject
    public InternalToExternalScanResultConverter(RxBleDeviceProvider deviceProvider) {
        this.deviceProvider = deviceProvider;
    }

    @Override
    public ScanResult apply(RxBleInternalScanResult rxBleInternalScanResult) {
        return new ScanResult(
                deviceProvider.getBleDevice(rxBleInternalScanResult.getBluetoothDevice().getAddress()),
                rxBleInternalScanResult.isLegacy(),
                rxBleInternalScanResult.isConnectable(),
                rxBleInternalScanResult.getDataStatus(),
                rxBleInternalScanResult.getPrimaryPhy(),
                rxBleInternalScanResult.getSecondaryPhy(),
                rxBleInternalScanResult.getAdvertisingSid(),
                rxBleInternalScanResult.getTxPower(),
                rxBleInternalScanResult.getRssi(),
                rxBleInternalScanResult.getPeriodicAdvertisingInterval(),
                rxBleInternalScanResult.getScanRecord(),
                rxBleInternalScanResult.getTimestampNanos(),
                rxBleInternalScanResult.getScanCallbackType()
        );
    }
}

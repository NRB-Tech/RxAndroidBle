package io.nrbtech.rxandroidble.internal.scan;


import io.nrbtech.rxandroidble.exceptions.BleScanException;
import io.nrbtech.rxandroidble.internal.util.LocationServicesStatus;
import io.nrbtech.rxandroidble.internal.util.RxBleAdapterWrapper;

import bleshadow.javax.inject.Inject;

@SuppressWarnings("WeakerAccess")
public class ScanPreconditionsVerifierApi18 implements ScanPreconditionsVerifier {

    final RxBleAdapterWrapper rxBleAdapterWrapper;

    final LocationServicesStatus locationServicesStatus;

    @Inject
    public ScanPreconditionsVerifierApi18(RxBleAdapterWrapper rxBleAdapterWrapper, LocationServicesStatus locationServicesStatus) {
        this.rxBleAdapterWrapper = rxBleAdapterWrapper;
        this.locationServicesStatus = locationServicesStatus;
    }

    @Override
    public void verify(boolean checkLocationProviderState) {
        if (!rxBleAdapterWrapper.hasBluetoothAdapter()) {
            throw new BleScanException(BleScanException.BLUETOOTH_NOT_AVAILABLE);
        } else if (!rxBleAdapterWrapper.isBluetoothEnabled()) {
            throw new BleScanException(BleScanException.BLUETOOTH_DISABLED);
        } else if (!locationServicesStatus.isLocationPermissionOk()) {
            throw new BleScanException(BleScanException.LOCATION_PERMISSION_MISSING);
        } else if (checkLocationProviderState && !locationServicesStatus.isLocationProviderOk()) {
            throw new BleScanException(BleScanException.LOCATION_SERVICES_DISABLED);
        }
    }
}

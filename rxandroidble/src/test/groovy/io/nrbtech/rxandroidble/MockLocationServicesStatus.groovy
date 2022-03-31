package io.nrbtech.rxandroidble

import io.nrbtech.rxandroidble.internal.util.LocationServicesStatus

class MockLocationServicesStatus implements LocationServicesStatus {
    boolean isLocationPermissionOk = true
    boolean isLocationProviderOk = true
    boolean isConnectPermissionOk = true
    boolean isScanPermissionOk = true

    MockLocationServicesStatus() {
    }

    @Override
    boolean isLocationPermissionOk() {
        return isLocationPermissionOk
    }

    @Override
    boolean isLocationProviderOk() {
        return isLocationProviderOk
    }

    @Override
    boolean isConnectPermissionOk() {
        return isConnectPermissionOk
    }

    @Override
    boolean isScanPermissionOk() {
        return isScanPermissionOk
    }
}

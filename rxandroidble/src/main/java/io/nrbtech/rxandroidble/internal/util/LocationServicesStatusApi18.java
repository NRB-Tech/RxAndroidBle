package io.nrbtech.rxandroidble.internal.util;

import bleshadow.javax.inject.Inject;

public class LocationServicesStatusApi18 implements LocationServicesStatus {

    @Inject
    LocationServicesStatusApi18() {

    }

    public boolean isLocationPermissionOk() {
        return true;
    }

    public boolean isLocationProviderOk() {
        return true;
    }

    public boolean isScanPermissionOk() {
        return true;
    }

    public boolean isConnectPermissionOk() {
        return true;
    }
}
package io.nrbtech.rxandroidble.internal.util;


public interface LocationServicesStatus {

    boolean isLocationPermissionOk();
    boolean isLocationProviderOk();
    boolean isScanPermissionOk();
    boolean isConnectPermissionOk();
}

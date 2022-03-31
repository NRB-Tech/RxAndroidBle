package io.nrbtech.rxandroidble.internal.scan;


import io.nrbtech.rxandroidble.exceptions.BleScanException;

public interface ScanPreconditionsVerifier {

    void verify(boolean checkLocationProviderState) throws BleScanException;
}

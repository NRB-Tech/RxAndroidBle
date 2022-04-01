package io.nrbtech.rxandroidble.internal.scan;

import io.nrbtech.rxandroidble.scan.ScanResultInterface;

public interface ScanFilterInterface {

    boolean isAllFieldsEmpty();

    boolean matches(ScanResultInterface scanResult);
}

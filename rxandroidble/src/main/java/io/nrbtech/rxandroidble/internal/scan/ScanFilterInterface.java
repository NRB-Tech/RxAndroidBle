package io.nrbtech.rxandroidble.internal.scan;

import io.nrbtech.rxandroidble.internal.ScanResultInterface;

public interface ScanFilterInterface {

    boolean isAllFieldsEmpty();

    boolean matches(ScanResultInterface scanResult);
}

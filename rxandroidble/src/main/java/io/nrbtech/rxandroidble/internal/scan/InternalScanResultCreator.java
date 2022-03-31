package io.nrbtech.rxandroidble.internal.scan;


import static io.nrbtech.rxandroidble.scan.ScanCallbackType.CALLBACK_TYPE_ALL_MATCHES;
import static io.nrbtech.rxandroidble.scan.ScanCallbackType.CALLBACK_TYPE_FIRST_MATCH;
import static io.nrbtech.rxandroidble.scan.ScanCallbackType.CALLBACK_TYPE_MATCH_LOST;
import static io.nrbtech.rxandroidble.scan.ScanCallbackType.CALLBACK_TYPE_UNKNOWN;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import io.nrbtech.rxandroidble.ClientScope;
import io.nrbtech.rxandroidble.internal.RxBleLog;
import io.nrbtech.rxandroidble.internal.util.ScanRecordParser;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanRecord;
import bleshadow.javax.inject.Inject;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@ClientScope
public class InternalScanResultCreator {

    private final ScanRecordParser scanRecordParser;

    @Inject
    public InternalScanResultCreator(ScanRecordParser scanRecordParser) {
        this.scanRecordParser = scanRecordParser;
    }

    public RxBleInternalScanResult create(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        final ScanRecord scanRecordObj = scanRecordParser.parseFromBytes(scanRecord);
        return new RxBleInternalScanResult(bluetoothDevice, null, null, null, null, null, null,
                null, rssi, null, scanRecordObj, System.nanoTime(),
                ScanCallbackType.CALLBACK_TYPE_UNSPECIFIED);
    }

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    private RxBleInternalScanResult create(ScanCallbackType scanCallbackType, ScanResult result) {
        final ScanRecordImplNativeWrapper scanRecord = new ScanRecordImplNativeWrapper(result.getScanRecord(), scanRecordParser);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new RxBleInternalScanResult(result.getDevice(), result.isLegacy(), result.isConnectable(), result.getDataStatus(),
                    result.getPrimaryPhy(), result.getSecondaryPhy(), result.getAdvertisingSid(), result.getTxPower(), result.getRssi(),
                    result.getPeriodicAdvertisingInterval(), scanRecord, result.getTimestampNanos(), scanCallbackType);
        } else {
            return new RxBleInternalScanResult(result.getDevice(), null, null, null, null, null, null,
                    null, result.getRssi(), null, scanRecord, result.getTimestampNanos(),
                    scanCallbackType);
        }
    }

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    public RxBleInternalScanResult create(ScanResult result) {
        return create(ScanCallbackType.CALLBACK_TYPE_BATCH, result);
    }

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    public RxBleInternalScanResult create(int callbackType, ScanResult result) {
        return create(toScanCallbackType(callbackType), result);
    }

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    private static ScanCallbackType toScanCallbackType(int callbackType) {
        switch (callbackType) {
            case ScanSettings.CALLBACK_TYPE_ALL_MATCHES:
                return CALLBACK_TYPE_ALL_MATCHES;
            case ScanSettings.CALLBACK_TYPE_FIRST_MATCH:
                return CALLBACK_TYPE_FIRST_MATCH;
            case ScanSettings.CALLBACK_TYPE_MATCH_LOST:
                return CALLBACK_TYPE_MATCH_LOST;
            default:
                RxBleLog.w("Unknown callback type %d -> check android.bluetooth.le.ScanSettings", callbackType);
                return CALLBACK_TYPE_UNKNOWN;
        }
    }
}

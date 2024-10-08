package io.nrbtech.rxandroidble.internal.scan;


import androidx.annotation.RestrictTo;

import io.nrbtech.rxandroidble.internal.operations.ScanOperationApi18;
import io.nrbtech.rxandroidble.internal.util.RxBleAdapterWrapper;
import io.nrbtech.rxandroidble.scan.ScanFilter;
import io.nrbtech.rxandroidble.scan.ScanSettings;

import bleshadow.javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ScanSetupBuilderImplApi18 implements ScanSetupBuilder {

    private final RxBleAdapterWrapper rxBleAdapterWrapper;
    private final InternalScanResultCreator internalScanResultCreator;
    private final ScanSettingsEmulator scanSettingsEmulator;

    @Inject
    ScanSetupBuilderImplApi18(
            RxBleAdapterWrapper rxBleAdapterWrapper,
            InternalScanResultCreator internalScanResultCreator,
            ScanSettingsEmulator scanSettingsEmulator
    ) {
        this.rxBleAdapterWrapper = rxBleAdapterWrapper;
        this.internalScanResultCreator = internalScanResultCreator;
        this.scanSettingsEmulator = scanSettingsEmulator;
    }

    @Override
    public ScanSetup build(ScanSettings scanSettings, ScanFilter... scanFilters) {
        final ObservableTransformer<RxBleInternalScanResult, RxBleInternalScanResult> scanModeTransformer
                = scanSettingsEmulator.emulateScanMode(scanSettings.getScanMode());
        final ObservableTransformer<RxBleInternalScanResult, RxBleInternalScanResult> callbackTypeTransformer
                = scanSettingsEmulator.emulateCallbackType(scanSettings.getCallbackType());
        return new ScanSetup(
                new ScanOperationApi18(
                        rxBleAdapterWrapper,
                        internalScanResultCreator,
                        new EmulatedScanFilterMatcher(scanFilters)
                ),
                new ObservableTransformer<RxBleInternalScanResult, RxBleInternalScanResult>() {
                    @Override
                    public Observable<RxBleInternalScanResult> apply(Observable<RxBleInternalScanResult> observable) {
                        return observable.compose(scanModeTransformer)
                                .compose(callbackTypeTransformer);
                    }
                }
        );
    }
}

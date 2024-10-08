package io.nrbtech.rxandroidble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.nrbtech.rxandroidble.RxBleAdapterStateObservable.BleAdapterState;
import io.nrbtech.rxandroidble.exceptions.BleException;
import io.nrbtech.rxandroidble.exceptions.BlePermissionException;
import io.nrbtech.rxandroidble.exceptions.BleScanException;
import io.nrbtech.rxandroidble.internal.RxBleDeviceProvider;
import io.nrbtech.rxandroidble.internal.RxBleLog;
import io.nrbtech.rxandroidble.internal.operations.LegacyScanOperation;
import io.nrbtech.rxandroidble.internal.operations.Operation;
import io.nrbtech.rxandroidble.internal.scan.RxBleInternalScanResult;
import io.nrbtech.rxandroidble.internal.scan.RxBleInternalScanResultLegacy;
import io.nrbtech.rxandroidble.internal.scan.ScanPreconditionsVerifier;
import io.nrbtech.rxandroidble.internal.scan.ScanSetup;
import io.nrbtech.rxandroidble.internal.scan.ScanSetupBuilder;
import io.nrbtech.rxandroidble.internal.serialization.ClientOperationQueue;
import io.nrbtech.rxandroidble.internal.util.CheckerScanPermission;
import io.nrbtech.rxandroidble.internal.util.ClientStateObservable;
import io.nrbtech.rxandroidble.internal.util.LocationServicesStatus;
import io.nrbtech.rxandroidble.internal.util.RxBleAdapterWrapper;
import io.nrbtech.rxandroidble.internal.util.BluetoothManagerWrapper;
import io.nrbtech.rxandroidble.internal.util.ScanRecordParser;
import io.nrbtech.rxandroidble.scan.BackgroundScanner;
import io.nrbtech.rxandroidble.scan.ScanFilter;
import io.nrbtech.rxandroidble.scan.ScanResult;
import io.nrbtech.rxandroidble.scan.ScanSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import bleshadow.dagger.Lazy;
import bleshadow.javax.inject.Inject;
import bleshadow.javax.inject.Named;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.functions.Supplier;

class RxBleClientImpl extends RxBleClient {

    @Deprecated
    public static final String TAG = "RxBleClient";
    final ClientOperationQueue operationQueue;
    private final ScanRecordParser scanRecordParser;
    private final RxBleDeviceProvider rxBleDeviceProvider;
    final ScanSetupBuilder scanSetupBuilder;
    final ScanPreconditionsVerifier scanPreconditionVerifier;
    final Function<RxBleInternalScanResult, ScanResult> internalToExternalScanResultMapFunction;
    private final ClientComponent.ClientComponentFinalizer clientComponentFinalizer;
    final Scheduler bluetoothInteractionScheduler;
    final Map<Set<UUID>, Observable<RxBleScanResult>> queuedScanOperations = new HashMap<>();
    private final BluetoothManagerWrapper bluetoothManagerWrapper;
    private final RxBleAdapterWrapper rxBleAdapterWrapper;
    private final Observable<BleAdapterState> rxBleAdapterStateObservable;
    private final LocationServicesStatus locationServicesStatus;
    private final Lazy<ClientStateObservable> lazyClientStateObservable;
    private final BackgroundScanner backgroundScanner;
    private final CheckerScanPermission checkerScanPermission;

    @Inject
    RxBleClientImpl(BluetoothManagerWrapper bluetoothManagerWrapper,
                    RxBleAdapterWrapper rxBleAdapterWrapper,
                    ClientOperationQueue operationQueue,
                    Observable<BleAdapterState> adapterStateObservable,
                    ScanRecordParser scanRecordParser,
                    LocationServicesStatus locationServicesStatus,
                    Lazy<ClientStateObservable> lazyClientStateObservable,
                    RxBleDeviceProvider rxBleDeviceProvider,
                    ScanSetupBuilder scanSetupBuilder,
                    ScanPreconditionsVerifier scanPreconditionVerifier,
                    Function<RxBleInternalScanResult, ScanResult> internalToExternalScanResultMapFunction,
                    @Named(ClientComponent.NamedSchedulers.BLUETOOTH_INTERACTION) Scheduler bluetoothInteractionScheduler,
                    ClientComponent.ClientComponentFinalizer clientComponentFinalizer,
                    BackgroundScanner backgroundScanner,
                    CheckerScanPermission checkerScanPermission) {
        this.operationQueue = operationQueue;
        this.bluetoothManagerWrapper = bluetoothManagerWrapper;
        this.rxBleAdapterWrapper = rxBleAdapterWrapper;
        this.rxBleAdapterStateObservable = adapterStateObservable;
        this.scanRecordParser = scanRecordParser;
        this.locationServicesStatus = locationServicesStatus;
        this.lazyClientStateObservable = lazyClientStateObservable;
        this.rxBleDeviceProvider = rxBleDeviceProvider;
        this.scanSetupBuilder = scanSetupBuilder;
        this.scanPreconditionVerifier = scanPreconditionVerifier;
        this.internalToExternalScanResultMapFunction = internalToExternalScanResultMapFunction;
        this.bluetoothInteractionScheduler = bluetoothInteractionScheduler;
        this.clientComponentFinalizer = clientComponentFinalizer;
        this.backgroundScanner = backgroundScanner;
        this.checkerScanPermission = checkerScanPermission;
    }

    @Override
    protected void finalize() throws Throwable {
        clientComponentFinalizer.onFinalize();
        super.finalize();
    }

    @Override
    public RxBleDevice getBleDevice(@NonNull String macAddress) {
        guardBluetoothAdapterAvailable();
        return rxBleDeviceProvider.getBleDevice(macAddress);
    }

    @Override
    public Set<RxBleDevice> getBondedDevices() {
        if (!locationServicesStatus.isConnectPermissionOk()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                throw new BlePermissionException(Manifest.permission.BLUETOOTH_CONNECT);
            }
            throw new BleException("Unexpected connect permission not OK");
        }
        guardBluetoothAdapterAvailable();
        Set<RxBleDevice> rxBleDevices = new HashSet<>();
        Set<BluetoothDevice> bluetoothDevices = rxBleAdapterWrapper.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            rxBleDevices.add(getBleDevice(bluetoothDevice.getAddress()));
        }

        return rxBleDevices;
    }

    @Override
    public Set<RxBleDevice> getConnectedPeripherals() {
        Set<RxBleDevice> rxBleDevices = new HashSet<>();
        List<BluetoothDevice> bluetoothDevices = bluetoothManagerWrapper.getConnectedPeripherals();
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            rxBleDevices.add(getBleDevice(bluetoothDevice.getAddress()));
        }

        return rxBleDevices;
    }

    @Override
    public Observable<ScanResult> scanBleDevices(final ScanSettings scanSettings, final ScanFilter... scanFilters) {
        return Observable.defer(new Supplier<ObservableSource<? extends ScanResult>>() {
            @Override
            public Observable<ScanResult> get() {
                scanPreconditionVerifier.verify(scanSettings.shouldCheckLocationProviderState());
                final ScanSetup scanSetup = scanSetupBuilder.build(scanSettings, scanFilters);
                final Operation<RxBleInternalScanResult> scanOperation = scanSetup.scanOperation;
                return operationQueue.queue(scanOperation)
                        .unsubscribeOn(bluetoothInteractionScheduler)
                        .compose(scanSetup.scanOperationBehaviourEmulatorTransformer)
                        .map(internalToExternalScanResultMapFunction)
                        .doOnNext(new Consumer<ScanResult>() {
                            @Override
                            public void accept(ScanResult scanResult) {
                                if (RxBleLog.getShouldLogScannedPeripherals()) RxBleLog.i("%s", scanResult);
                            }
                        })
                        .mergeWith(RxBleClientImpl.this.<ScanResult>bluetoothAdapterOffExceptionObservable());
            }
        });
    }

    @Override
    public BackgroundScanner getBackgroundScanner() {
        return backgroundScanner;
    }

    @Override
    @Deprecated
    public Observable<RxBleScanResult> scanBleDevices(@Nullable final UUID... filterServiceUUIDs) {
        return Observable.defer(new Supplier<ObservableSource<? extends RxBleScanResult>>() {
            @Override
            public ObservableSource<? extends RxBleScanResult> get() {
                scanPreconditionVerifier.verify(true);
                return initializeScan(filterServiceUUIDs);
            }
        });
    }

    private Set<UUID> toDistinctSet(@Nullable UUID[] uuids) {
        if (uuids == null) uuids = new UUID[0];
        return new HashSet<>(Arrays.asList(uuids));
    }

    Observable<RxBleScanResult> initializeScan(@Nullable UUID[] filterServiceUUIDs) {
        final Set<UUID> filteredUUIDs = toDistinctSet(filterServiceUUIDs);

        synchronized (queuedScanOperations) {
            Observable<RxBleScanResult> matchingQueuedScan = queuedScanOperations.get(filteredUUIDs);

            if (matchingQueuedScan == null) {
                matchingQueuedScan = createScanOperationApi18(filterServiceUUIDs);
                queuedScanOperations.put(filteredUUIDs, matchingQueuedScan);
            }

            return matchingQueuedScan;
        }
    }

    /**
     * This {@link Observable} will not emit values by design. It may only emit {@link BleScanException} if
     * bluetooth adapter is turned down.
     */
    <T> Observable<T> bluetoothAdapterOffExceptionObservable() {
        return rxBleAdapterStateObservable
                .filter(new Predicate<BleAdapterState>() {
                    @Override
                    public boolean test(BleAdapterState state) {
                        return state != BleAdapterState.STATE_ON;
                    }
                })
                .firstElement()
                .flatMap(new Function<BleAdapterState, MaybeSource<T>>() {
                    @Override
                    public MaybeSource<T> apply(BleAdapterState bleAdapterState) {
                        return Maybe.error(new BleScanException(BleScanException.BLUETOOTH_DISABLED));
                    }
                })
                .toObservable();
    }

    RxBleScanResult convertToPublicScanResult(RxBleInternalScanResultLegacy scanResult) {
        final BluetoothDevice bluetoothDevice = scanResult.getBluetoothDevice();
        final RxBleDevice bleDevice = getBleDevice(bluetoothDevice.getAddress());
        return new RxBleScanResult(bleDevice, scanResult.getRssi(), scanResult.getScanRecord());
    }

    private Observable<RxBleScanResult> createScanOperationApi18(@Nullable final UUID[] filterServiceUUIDs) {
        final Set<UUID> filteredUUIDs = toDistinctSet(filterServiceUUIDs);
        final LegacyScanOperation
                scanOperation = new LegacyScanOperation(filterServiceUUIDs, rxBleAdapterWrapper, scanRecordParser);
        return operationQueue.queue(scanOperation)
                .doFinally(new Action() {
                    @Override
                    public void run() {
                        synchronized (queuedScanOperations) {
                            queuedScanOperations.remove(filteredUUIDs);
                        }
                    }
                })
                .mergeWith(this.<RxBleInternalScanResultLegacy>bluetoothAdapterOffExceptionObservable())
                .map(new Function<RxBleInternalScanResultLegacy, RxBleScanResult>() {
                    @Override
                    public RxBleScanResult apply(RxBleInternalScanResultLegacy scanResult) {
                        return convertToPublicScanResult(scanResult);
                    }
                })
                .doOnNext(new Consumer<RxBleScanResult>() {
                    @Override
                    public void accept(RxBleScanResult rxBleScanResult) {
                        RxBleLog.i("%s", rxBleScanResult);
                    }
                })
                .share();
    }

    private void guardBluetoothAdapterAvailable() {
        if (!rxBleAdapterWrapper.hasBluetoothAdapter()) {
            throw new UnsupportedOperationException("RxAndroidBle library needs a BluetoothAdapter to be available in the system to work."
            + " If this is a test on an emulator then you can use 'https://github.com/nrb-tech/RxAndroidBle/tree/master/mockrxandroidble'");
        }
    }

    @Override
    public Observable<State> observeStateChanges() {
        return lazyClientStateObservable.get();
    }

    @Override
    public State getState() {
        if (!rxBleAdapterWrapper.hasBluetoothAdapter()) {
            return State.BLUETOOTH_NOT_AVAILABLE;
        }
        if (!locationServicesStatus.isScanPermissionOk()) {
            return State.BLUETOOTH_SCAN_PERMISSION_NOT_GRANTED;
        }
        if (!locationServicesStatus.isLocationPermissionOk()) {
            return State.LOCATION_PERMISSION_NOT_GRANTED;
        }
        if (!rxBleAdapterWrapper.isBluetoothEnabled()) {
            return State.BLUETOOTH_NOT_ENABLED;
        }
        if (!locationServicesStatus.isLocationProviderOk()) {
            return State.LOCATION_SERVICES_NOT_ENABLED;
        } else {
            return State.READY;
        }
    }

    @Override
    public boolean isScanRuntimePermissionGranted() {
        return checkerScanPermission.isScanRuntimePermissionGranted();
    }

    @Override
    public String[] getRecommendedScanRuntimePermissions() {
        return checkerScanPermission.getRecommendedScanRuntimePermissions();
    }

    @Override
    public boolean isConnectRuntimePermissionGranted() {
        return checkerScanPermission.isConnectRuntimePermissionGranted();
    }

    @Override
    public String[] getRecommendedConnectRuntimePermissions() {
        return checkerScanPermission.getRecommendedConnectRuntimePermissions();
    }
}

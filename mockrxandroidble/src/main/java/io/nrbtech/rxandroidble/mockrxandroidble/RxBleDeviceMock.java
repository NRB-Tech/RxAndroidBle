package io.nrbtech.rxandroidble.mockrxandroidble;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.RxBleDeviceServices;
import io.nrbtech.rxandroidble.Timeout;
import io.nrbtech.rxandroidble.exceptions.BleAlreadyConnectedException;
import io.nrbtech.rxandroidble.exceptions.BleException;
import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.RxBleCharacteristicReadCallback;
import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.RxBleCharacteristicWriteCallback;
import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.RxBleDescriptorReadCallback;
import io.nrbtech.rxandroidble.mockrxandroidble.callbacks.RxBleDescriptorWriteCallback;
import io.nrbtech.rxandroidble.scan.ScanRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

import static io.nrbtech.rxandroidble.RxBleConnection.RxBleConnectionState.CONNECTED;
import static io.nrbtech.rxandroidble.RxBleConnection.RxBleConnectionState.CONNECTING;
import static io.nrbtech.rxandroidble.RxBleConnection.RxBleConnectionState.DISCONNECTED;

public class RxBleDeviceMock implements RxBleDevice {

    private ReplaySubject<RxBleConnection> connectionSubject;
    private RxBleConnection rxBleConnection;
    private BehaviorSubject<RxBleConnection.RxBleConnectionState> connectionStateBehaviorSubject = BehaviorSubject.createDefault(
            DISCONNECTED
    );
    private String name;
    private String macAddress;
    private Integer rssi;
    private byte[] legacyScanRecord;
    private ScanRecord scanRecord;
    private List<UUID> advertisedUUIDs;
    private BluetoothDevice bluetoothDevice;
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private boolean isConnectable;

    private RxBleDeviceMock(String name,
                            String macAddress,
                            @Nullable BluetoothDevice bluetoothDevice,
                            RxBleConnectionMock connectionMock,
                            boolean isConnectable) {
        this.name = name;
        this.macAddress = macAddress;
        this.rssi = connectionMock.getRssi();
        this.advertisedUUIDs = connectionMock.getServiceUuids();
        this.bluetoothDevice = bluetoothDevice;
        this.rxBleConnection = connectionMock;
        this.isConnectable = isConnectable;
        connectionMock.setDeviceMock(this);
    }

    public RxBleDeviceMock(String name,
                           String macAddress,
                           byte[] scanRecord,
                           Integer rssi,
                           RxBleDeviceServices rxBleDeviceServices,
                           Map<UUID, Observable<byte[]>> characteristicNotificationSources,
                           @Nullable BluetoothDevice bluetoothDevice,
                           boolean isConnectable) {
        this(
                name,
                macAddress,
                bluetoothDevice,
                new RxBleConnectionMock(
                        rxBleDeviceServices,
                        rssi,
                        characteristicNotificationSources,
                        new HashMap<UUID, RxBleCharacteristicReadCallback>(),
                        new HashMap<UUID, RxBleCharacteristicWriteCallback>(),
                        new HashMap<UUID, Map<UUID, RxBleDescriptorReadCallback>>(),
                        new HashMap<UUID, Map<UUID, RxBleDescriptorWriteCallback>>()),
                isConnectable
                );
        this.legacyScanRecord = scanRecord;
    }

    public RxBleDeviceMock(String name,
                           String macAddress,
                           ScanRecord scanRecord,
                           @Nullable BluetoothDevice bluetoothDevice,
                           RxBleConnectionMock connectionMock,
                           boolean isConnectable
    ) {
        this(
                name,
                macAddress,
                bluetoothDevice,
                connectionMock,
                isConnectable
        );
        this.scanRecord = scanRecord;
    }

    public static class Builder {

        private String deviceName;
        private String deviceMacAddress;
        private byte[] legacyScanRecord;
        private ScanRecord scanRecord;
        private BluetoothDevice bluetoothDevice;
        RxBleConnectionMock connectionMock;
        RxBleConnectionMock.Builder connectionMockBuilder;
        boolean isConnectable = true;

        /**
         * Build a new {@link RxBleDevice}.
         * <p>
         * Calling {@link #scanRecord}, {@link #rssi} and {@link #deviceMacAddress}
         * is required before calling {@link #build()}. All other methods
         * are optional.
         */
        public Builder() {
            this.connectionMockBuilder = new RxBleConnectionMock.Builder();
        }

        /**
         * Create the {@link RxBleDeviceMock} instance using the configured values.
         */
        public RxBleDevice build() {
            if (this.deviceMacAddress == null) throw new IllegalStateException("DeviceMacAddress required."
                    + " DeviceBuilder#deviceMacAddress should be called.");
            if (this.scanRecord == null && this.legacyScanRecord == null)
                throw new IllegalStateException("ScanRecord required. DeviceBuilder#scanRecord should be called.");

            RxBleConnectionMock connMock = connectionMock == null ? connectionMockBuilder.build() : connectionMock;
            // legacy
            if (scanRecord == null) {
                RxBleDeviceMock rxBleDeviceMock = new RxBleDeviceMock(deviceName,
                        deviceMacAddress,
                        legacyScanRecord,
                        connMock.getRssi(),
                        connMock.getRxBleDeviceServices(),
                        connMock.getCharacteristicNotificationSources(),
                        bluetoothDevice,
                        isConnectable);
                for (UUID service : connMock.getServiceUuids()) {
                    rxBleDeviceMock.addAdvertisedUUID(service);
                }
                return rxBleDeviceMock;
            }
            return new RxBleDeviceMock(deviceName,
                    deviceMacAddress,
                    scanRecord,
                    bluetoothDevice,
                    connMock,
                    isConnectable
                    );
        }

        /**
         * Set a device mac address. Calling this method is required.
         */
        public Builder deviceMacAddress(@NonNull String deviceMacAddress) {
            this.deviceMacAddress = deviceMacAddress;
            return this;
        }

        /**
         * Set a device name. Calling this method is not required.
         */
        public Builder deviceName(@NonNull String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        /**
         * Sets a bluetooth device. Calling this method is not required.
         */
        public Builder bluetoothDevice(@NonNull BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
            return this;
        }

        /**
         * Set a BLE scan record. Calling either this method or the other {@link #scanRecord(ScanRecord)} method is required.
         */
        public Builder scanRecord(@NonNull byte[] scanRecord) {
            this.legacyScanRecord = scanRecord;
            return this;
        }

        /**
         * Set a BLE scan record. Calling either this method or the other {@link #scanRecord(byte[])}} method is required.
         */
        public Builder scanRecord(@NonNull ScanRecord scanRecord) {
            this.scanRecord = scanRecord;
            return this;
        }

        /**
         * Set a BLE connection mock. Calling this method is required.
         */
        public Builder connection(@NonNull RxBleConnectionMock connectionMock) {
            this.connectionMock = connectionMock;
            return this;
        }

        /**
         * Set if the mock is connectable
         */
        public Builder isConnectable(boolean isConnectable) {
            this.isConnectable = isConnectable;
            return this;
        }
    }

    public void addAdvertisedUUID(UUID advertisedUUID) {
        advertisedUUIDs.add(advertisedUUID);
    }

    @Override
    public Observable<RxBleConnection> establishConnection(boolean autoConnect) {
        return Observable.defer(new Supplier<ObservableSource<? extends RxBleConnection>>() {
            @Override
            public ObservableSource<? extends RxBleConnection> get() {
                if (isConnected.compareAndSet(false, true)) {
                    return RxBleDeviceMock.this.emitConnectionWithoutCompleting()
                            .doOnSubscribe(new Consumer<Disposable>() {
                                @Override
                                public void accept(Disposable disposable) throws Exception {
                                    connectionStateBehaviorSubject.onNext(CONNECTING);
                                }
                            })
                            .doOnNext(new Consumer<RxBleConnection>() {
                                @Override
                                public void accept(RxBleConnection rxBleConnection) throws Exception {
                                    connectionStateBehaviorSubject.onNext(CONNECTED);
                                }
                            })
                            .doFinally(new Action() {
                                @Override
                                public void run() {
                                    connectionStateBehaviorSubject.onNext(DISCONNECTED);
                                    isConnected.set(false);
                                }
                            });
                } else {
                    return Observable.error(new BleAlreadyConnectedException(macAddress));
                }
            }
        });
    }

    @Override
    public Observable<RxBleConnection> establishConnection(boolean autoConnect, Timeout operationTimeout) {
        return establishConnection(autoConnect);
    }

    private Observable<RxBleConnection> emitConnectionWithoutCompleting() {
        connectionSubject = ReplaySubject.createWithSize(1);
        connectionSubject.onNext(rxBleConnection);
        return connectionSubject.doFinally(new Action() {
            @Override
            public void run() throws Exception {
                connectionSubject = null;
            }
        });
    }

    public void disconnectWithException(BleException exception) {
        if (connectionSubject != null) {
            connectionSubject.onError(exception);
        }
    }

    public List<UUID> getAdvertisedUUIDs() {
        return advertisedUUIDs;
    }

    @Override
    public RxBleConnection.RxBleConnectionState getConnectionState() {
        return observeConnectionStateChanges().blockingFirst();
    }

    @Override
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        if (bluetoothDevice != null) {
            return bluetoothDevice;
        }
        throw new IllegalStateException("Mock is not configured to return a BluetoothDevice");
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getRssi() {
        return rssi;
    }

    public byte[] getLegacyScanRecord() {
        return legacyScanRecord;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    public boolean getIsConnectable() {
        return isConnectable;
    }

    @Override
    public Observable<RxBleConnection.RxBleConnectionState> observeConnectionStateChanges() {
        return connectionStateBehaviorSubject.distinctUntilChanged();
    }

    @Override
    public String toString() {
        return "RxBleDeviceImpl{" + "bluetoothDevice=" + name + '(' + macAddress + ')' + '}';
    }
}

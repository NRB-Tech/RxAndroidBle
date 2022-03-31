package io.nrbtech.rxandroidble.mockrxandroidble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.nrbtech.rxandroidble.RxBleClient;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.RxBleScanResult;
import io.nrbtech.rxandroidble.scan.BackgroundScanner;
import io.nrbtech.rxandroidble.scan.ScanCallbackType;
import io.nrbtech.rxandroidble.scan.ScanFilter;
import io.nrbtech.rxandroidble.scan.ScanRecord;
import io.nrbtech.rxandroidble.scan.ScanResult;
import io.nrbtech.rxandroidble.scan.ScanSettings;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.subjects.ReplaySubject;

/**
 * A mocked {@link RxBleClient}. Callers supply device parameters such as services,
 * characteristics and descriptors the mocked client returns them upon request.
 */
public class RxBleClientMock extends RxBleClient {

    public static class Builder {

        private ReplaySubject<RxBleDeviceMock> discoverableDevicesSubject;
        private Set<RxBleDevice> bondedDevices;
        private Set<RxBleDevice> connectedPeripherals;

        /**
         * Build a new {@link RxBleClientMock}.
         */
        public Builder() {
            this.discoverableDevicesSubject = ReplaySubject.create();
            this.bondedDevices = new HashSet<>();
            this.connectedPeripherals = new HashSet<>();
        }

        public Builder setDeviceDiscoveryObservable(@NonNull Observable<RxBleDeviceMock> discoverableDevicesObservable) {
            discoverableDevicesObservable.subscribe(this.discoverableDevicesSubject);
            return this;
        }

        /**
         * Add a {@link RxBleDevice} to the mock client.
         *
         * @param rxBleDevice device that the mocked client should contain. Use {@link RxBleDeviceMock.Builder} to create them.
         */
        public Builder addDevice(@NonNull RxBleDevice rxBleDevice) {
            this.discoverableDevicesSubject.onNext((RxBleDeviceMock) rxBleDevice);
            return this;
        }

        /**
         * Add a {@link RxBleDevice} to the list of bonded devices.
         *
         * @param rxBleDevice device that the mocked client should contain. Use {@link RxBleDeviceMock.Builder} to create them.
         */
        public Builder addBondedDevice(@NonNull RxBleDevice rxBleDevice) {
            bondedDevices.add(rxBleDevice);
            return this;
        }

        /**
         * Add a {@link RxBleDevice} to the list of connected devices.
         *
         * @param rxBleDevice device that the mocked client should contain. Use {@link RxBleDeviceMock.Builder} to create them.
         */
        public Builder addConnectedPeripheral(@NonNull RxBleDevice rxBleDevice) {
            connectedPeripherals.add(rxBleDevice);
            return this;
        }

        /**
         * Create the {@link RxBleClientMock} instance using the configured values.
         */
        public RxBleClientMock build() {
            return new RxBleClientMock(this);
        }
    }

    public static class CharacteristicsBuilder {

        private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;

        /**
         * Build a new {@link BluetoothGattCharacteristic} list.
         * Should be used in pair with {@link RxBleDeviceMock.Builder#addService}
         */
        public CharacteristicsBuilder() {
            this.bluetoothGattCharacteristics = new ArrayList<>();
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic}
         *
         * @param characteristic        The characteristic to add
         */
        public CharacteristicsBuilder addCharacteristic(BluetoothGattCharacteristic characteristic) {
            this.bluetoothGattCharacteristics.add(characteristic);
            return this;
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         * @param properties  OR-ed {@link BluetoothGattCharacteristic} property constants
         * @param descriptors list of characteristic descriptors. Use {@link DescriptorsBuilder} to create them.
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data,
                                                        int properties,
                                                        List<BluetoothGattDescriptor> descriptors) {
            BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(uuid, properties, 0);
            for (BluetoothGattDescriptor descriptor : descriptors) {
                characteristic.addDescriptor(descriptor);
            }
            characteristic.setValue(data);
            return addCharacteristic(characteristic);
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         * @param properties  OR-ed {@link BluetoothGattCharacteristic} property constants
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data,
                                                        int properties) {
            return addCharacteristic(uuid, data, properties, new ArrayList<BluetoothGattDescriptor>());
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         * @param descriptors list of characteristic descriptors. Use {@link DescriptorsBuilder} to create them.
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data,
                                                        List<BluetoothGattDescriptor> descriptors) {
            return addCharacteristic(uuid, data, 0, descriptors);
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         * @param descriptors list of characteristic descriptors. Use {@link DescriptorsBuilder} to create them.
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data,
                                                        BluetoothGattDescriptor... descriptors) {
            return addCharacteristic(uuid, data, 0, descriptors);
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         * @param properties  OR-ed {@link BluetoothGattCharacteristic} property constants
         * @param descriptors list of characteristic descriptors. Use {@link DescriptorsBuilder} to create them.
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data,
                                                        int properties,
                                                        BluetoothGattDescriptor... descriptors) {
            return addCharacteristic(uuid, data, properties, Arrays.asList(descriptors));
        }

        /**
         * Adds a {@link BluetoothGattCharacteristic} with specified parameters.
         *
         * @param uuid        characteristic UUID
         * @param data        locally stored value of the characteristic
         */
        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid,
                                                        @NonNull byte[] data) {
            return addCharacteristic(uuid, data, 0);
        }

        /**
         * Create the {@link List} of {@link BluetoothGattCharacteristic} using the configured values.
         */
        public List<BluetoothGattCharacteristic> build() {
            return bluetoothGattCharacteristics;
        }
    }

    public static class DescriptorsBuilder {

        private List<BluetoothGattDescriptor> bluetoothGattDescriptors;

        /**
         * Build a new {@link BluetoothGattDescriptor} list.
         * Should be used in pair with {@link CharacteristicsBuilder#addCharacteristic}
         */
        public DescriptorsBuilder() {
            this.bluetoothGattDescriptors = new ArrayList<>();
        }

        /**
         * Adds a {@link BluetoothGattDescriptor}.
         *
         * @param descriptor the descriptor
         */
        public DescriptorsBuilder addDescriptor(@NonNull BluetoothGattDescriptor descriptor) {
            bluetoothGattDescriptors.add(descriptor);
            return this;
        }

        /**
         * Adds a {@link BluetoothGattDescriptor} with specified parameters.
         *
         * @param uuid descriptor UUID
         * @param data locally stored value of the descriptor
         */
        public DescriptorsBuilder addDescriptor(@NonNull UUID uuid, @NonNull byte[] data) {
            BluetoothGattDescriptor bluetoothGattDescriptor = new BluetoothGattDescriptor(uuid, 0);
            bluetoothGattDescriptor.setValue(data);
            return addDescriptor(bluetoothGattDescriptor);
        }

        /**
         * Create the  {@link List} of {@link BluetoothGattDescriptor} using the configured values.
         */
        public List<BluetoothGattDescriptor> build() {
            return bluetoothGattDescriptors;
        }
    }

    private Set<RxBleDevice> bondedDevices;
    private Set<RxBleDevice> connectedPeripherals;
    private ReplaySubject<RxBleDeviceMock> discoveredDevicesSubject;

    private RxBleClientMock(Builder builder) {
        bondedDevices = builder.bondedDevices;
        connectedPeripherals = builder.connectedPeripherals;
        discoveredDevicesSubject = builder.discoverableDevicesSubject;
    }

    @Override
    public RxBleDevice getBleDevice(@NonNull final String macAddress) {

        Object[] rxBleDevices = discoveredDevicesSubject
                .getValues();
        for (Object device : rxBleDevices) {
            if (((RxBleDevice) device).getMacAddress().equals(macAddress)) {
                return (RxBleDevice) device;
            }
        }
        throw new IllegalStateException("Mock is not configured for a given mac address. Use Builder#addDevice method.");
    }

    @Override
    public Set<RxBleDevice> getBondedDevices() {
        return bondedDevices;
    }

    @Override
    public Set<RxBleDevice> getConnectedPeripherals() {
        return connectedPeripherals;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Observable<RxBleScanResult> scanBleDevices(@Nullable UUID... filterServiceUUIDs) {
        return createScanOperation(filterServiceUUIDs);
    }

    private static RxBleScanResult convertToPublicLegacyScanResult(RxBleDevice bleDevice, Integer rssi, byte[] scanRecord) {
        return new RxBleScanResult(bleDevice, rssi, scanRecord);
    }

    @NonNull
    private Observable<RxBleScanResult> createScanOperation(@Nullable final UUID[] filterServiceUUIDs) {
        return discoveredDevicesSubject
                .filter(new Predicate<RxBleDeviceMock>() {
                    @Override
                    public boolean test(RxBleDeviceMock rxBleDevice) {
                        return RxBleClientMock.filterDevice(rxBleDevice, filterServiceUUIDs);
                    }
                })
                .map(new Function<RxBleDeviceMock, RxBleScanResult>() {
                    @Override
                    public RxBleScanResult apply(RxBleDeviceMock rxBleDeviceMock) {
                        return RxBleClientMock.this.createRxBleScanResult(rxBleDeviceMock);
                    }
                });
    }

    @NonNull
    private RxBleScanResult createRxBleScanResult(RxBleDeviceMock rxBleDeviceMock) {
        return convertToPublicLegacyScanResult(rxBleDeviceMock, rxBleDeviceMock.getRssi(), rxBleDeviceMock.getLegacyScanRecord());
    }

    private static boolean filterDevice(RxBleDevice rxBleDevice, @Nullable UUID[] filterServiceUUIDs) {

        if (filterServiceUUIDs == null || filterServiceUUIDs.length == 0) {
            return true;
        }

        List<UUID> advertisedUUIDs = ((RxBleDeviceMock) rxBleDevice).getAdvertisedUUIDs();

        for (UUID desiredUUID : filterServiceUUIDs) {

            if (!advertisedUUIDs.contains(desiredUUID)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Observable<ScanResult> scanBleDevices(ScanSettings scanSettings, ScanFilter... scanFilters) {
        return createScanOperation(scanSettings, scanFilters);
    }

    @NonNull
    private Observable<ScanResult> createScanOperation(ScanSettings scanSettings, final ScanFilter... scanFilters) {
        return discoveredDevicesSubject
                .map(new Function<RxBleDeviceMock, ScanResult>() {
                    @Override
                    public ScanResult apply(RxBleDeviceMock rxBleDeviceMock) {
                        return RxBleClientMock.this.createScanResult(rxBleDeviceMock);
                    }
                })
                .filter(new Predicate<ScanResult>() {
                    @Override
                    public boolean test(ScanResult scanResult) {
                        for (ScanFilter filter : scanFilters) {
                            if (!filter.matches(scanResult)) {
                                return false;
                            }
                        }
                        return true;
                    }
                });
    }

    @NonNull
    private ScanResult createScanResult(RxBleDeviceMock rxBleDeviceMock) {
        return convertToPublicScanResult(
                rxBleDeviceMock,
                null,
                rxBleDeviceMock.getIsConnectable(),
                null,
                null,
                null,
                null,
                null,
                rxBleDeviceMock.getRssi(),
                null,
                rxBleDeviceMock.getScanRecord()
        );
    }

    @NonNull
    private static ScanResult convertToPublicScanResult(RxBleDevice bleDevice, Boolean isLegacy, Boolean isConnectableStatus,
                                                        Integer dataStatus, Integer primaryPhy, Integer secondaryPhy,
                                                        Integer advertisingSid, Integer txPower, int rssi,
                                                        Integer periodicAdvertisingInterval, ScanRecord scanRecord) {
        return new ScanResult(
                bleDevice,
                isLegacy,
                isConnectableStatus,
                dataStatus,
                primaryPhy,
                secondaryPhy,
                advertisingSid,
                txPower,
                rssi,
                periodicAdvertisingInterval,
                scanRecord,
                System.currentTimeMillis() * 1000000,
                ScanCallbackType.CALLBACK_TYPE_FIRST_MATCH);
    }

    private static boolean maskedDataEquals(@NonNull byte[] data1, @NonNull byte[] data2, @Nullable byte[] mask) {
        if (mask == null) {
            return Arrays.equals(data1, data2);
        } else {
            if (data1.length != data2.length || data1.length != mask.length) {
                return false;
            }
            for (int i = 0; i < data1.length; i++) {
                if ((data1[i] & mask[i]) != (data2[i] & mask[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private static byte[] getDataFromUUID(@NonNull UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public BackgroundScanner getBackgroundScanner() {
        throw new UnsupportedOperationException("Background scanning API is not supported by the mock.");
    }

    @Override
    public Observable<State> observeStateChanges() {
        return Observable.never();
    }

    @Override
    public State getState() {
        return State.READY;
    }

    @Override
    public boolean isScanRuntimePermissionGranted() {
        return true;
    }

    @Override
    public String[] getRecommendedScanRuntimePermissions() {
        return new String[0];
    }

    @Override
    public boolean isConnectRuntimePermissionGranted() {
        return true;
    }

    @Override
    public String[] getRecommendedConnectRuntimePermissions() {
        return new String[0];
    }
}

package com.polidea.rxandroidble.mockrxandroidble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleDeviceServices;
import com.polidea.rxandroidble.RxBleScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rx.Observable;

public class RxBleClientMock implements RxBleClient {

    public static class Builder {

        private Integer rssi;
        private String deviceName;
        private String deviceMacAddress;
        private byte[] scanRecord;
        private RxBleDeviceServices rxBleDeviceServices;
        private Map<UUID, Observable<byte[]>> characteristicNotificationSources;

        public Builder() {
            this.rxBleDeviceServices = new RxBleDeviceServices(new ArrayList<>());
            this.characteristicNotificationSources = new HashMap<>();
        }

        public Builder addService(UUID uuid, List<BluetoothGattCharacteristic> characteristics) {
            BluetoothGattService bluetoothGattService = new BluetoothGattService(uuid, 0);
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                bluetoothGattService.addCharacteristic(characteristic);
            }
            rxBleDeviceServices.getBluetoothGattServices().add(bluetoothGattService);
            return this;
        }

        public RxBleClientMock build() {
            if (this.rssi == null) throw new IllegalStateException("rssi can't be null");
            if (this.deviceMacAddress == null) throw new IllegalStateException("deviceMacAddress can't be null");
            if (this.scanRecord == null) throw new IllegalStateException("scanRecord can't be null");
            return new RxBleClientMock(this);
        }

        public Builder deviceMacAddress(@NonNull String deviceMacAddress) {
            this.deviceMacAddress = deviceMacAddress;
            return this;
        }

        public Builder deviceName(@NonNull String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder notificationSource(UUID characteristicUUID, Observable<byte[]> sourceObservable) {
            characteristicNotificationSources.put(characteristicUUID, sourceObservable);
            return this;
        }

        public Builder rssi(@NonNull Integer rssi) {
            this.rssi = rssi;
            return this;
        }

        public Builder scanRecord(@NonNull byte[] scanRecord) {
            this.scanRecord = scanRecord;
            return this;
        }
    }

    public static class CharacteristicsBuilder {

        private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;

        public CharacteristicsBuilder() {
            this.bluetoothGattCharacteristics = new ArrayList<>();
        }

        public CharacteristicsBuilder addCharacteristic(@NonNull UUID uuid, @NonNull byte[] data, List<BluetoothGattDescriptor> descriptors) {
            BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
            for (BluetoothGattDescriptor descriptor : descriptors) {
                characteristic.addDescriptor(descriptor);
            }
            characteristic.setValue(data);
            this.bluetoothGattCharacteristics.add(characteristic);
            return this;
        }

        public List<BluetoothGattCharacteristic> build() {
            return bluetoothGattCharacteristics;
        }
    }

    public static class DescriptorsBuilder {

        private List<BluetoothGattDescriptor> bluetoothGattDescriptors;

        public DescriptorsBuilder() {
            this.bluetoothGattDescriptors = new ArrayList<>();
        }

        public DescriptorsBuilder addDescriptor(@NonNull UUID uuid, @NonNull byte[] data) {
            BluetoothGattDescriptor bluetoothGattDescriptor = new BluetoothGattDescriptor(uuid, 0);
            bluetoothGattDescriptor.setValue(data);
            bluetoothGattDescriptors.add(bluetoothGattDescriptor);
            return this;
        }

        public List<BluetoothGattDescriptor> build() {
            return bluetoothGattDescriptors;
        }
    }

    private RxBleDevice rxBleDevice;
    private Integer rssi;
    private byte[] scanRecord;
    private RxBleConnectionMock rxBleConnectionMock;

    private RxBleClientMock(Builder builder) {
        rxBleConnectionMock = new RxBleConnectionMock(builder.rxBleDeviceServices, builder.rssi, builder.characteristicNotificationSources);
        rxBleDevice = new RxBleDeviceMock(builder.deviceName, builder.deviceMacAddress, rxBleConnectionMock);
        rssi = builder.rssi;
        scanRecord = builder.scanRecord;
    }

    public void disconnect() {
        rxBleConnectionMock.simulateDeviceDisconnect();
    }

    @Override
    public RxBleDevice getBleDevice(String bluetoothAddress) {
        return rxBleDevice;
    }

    @Override
    public Observable<RxBleScanResult> scanBleDevices(@Nullable UUID[] filterServiceUUIDs) {
        return Observable.just(new RxBleScanResult(rxBleDevice, rssi, scanRecord));
    }
}

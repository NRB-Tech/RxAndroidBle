package io.nrbtech.rxandroidble.internal;

import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.ClientScope;
import io.nrbtech.rxandroidble.internal.cache.DeviceComponentCache;

import java.util.Map;

import bleshadow.javax.inject.Inject;
import bleshadow.javax.inject.Provider;

@ClientScope
public class RxBleDeviceProvider {

    private final Map<String, DeviceComponent> cachedDeviceComponents;
    private final Provider<DeviceComponent.Builder> deviceComponentBuilder;

    @Inject
    public RxBleDeviceProvider(DeviceComponentCache deviceComponentCache, Provider<DeviceComponent.Builder> deviceComponentBuilder) {
        this.cachedDeviceComponents = deviceComponentCache;
        this.deviceComponentBuilder = deviceComponentBuilder;
    }

    public RxBleDevice getBleDevice(String macAddress) {
        final DeviceComponent cachedDeviceComponent = cachedDeviceComponents.get(macAddress);

        if (cachedDeviceComponent != null) {
            return cachedDeviceComponent.provideDevice();
        }

        synchronized (cachedDeviceComponents) {
            final DeviceComponent secondCheckRxBleDevice = cachedDeviceComponents.get(macAddress);

            if (secondCheckRxBleDevice != null) {
                return secondCheckRxBleDevice.provideDevice();
            }

            final DeviceComponent deviceComponent =
                    deviceComponentBuilder.get()
                            .macAddress(macAddress)
                            .build();

            final RxBleDevice newRxBleDevice = deviceComponent.provideDevice();
            cachedDeviceComponents.put(macAddress, deviceComponent);
            return newRxBleDevice;
        }
    }
}

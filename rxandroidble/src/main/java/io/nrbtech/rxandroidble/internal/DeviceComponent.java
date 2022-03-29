package io.nrbtech.rxandroidble.internal;

import static io.nrbtech.rxandroidble.internal.DeviceModule.MAC_ADDRESS;

import io.nrbtech.rxandroidble.RxBleDevice;

import bleshadow.dagger.BindsInstance;
import bleshadow.dagger.Subcomponent;
import bleshadow.javax.inject.Named;

@DeviceScope
@Subcomponent(modules = {DeviceModule.class})
public interface DeviceComponent {

    @Subcomponent.Builder
    interface Builder {
        DeviceComponent build();

        @BindsInstance
        Builder macAddress(@Named(MAC_ADDRESS) String deviceMacAddress);
    }

    @DeviceScope
    RxBleDevice provideDevice();
}

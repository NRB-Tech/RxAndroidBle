package io.nrbtech.rxandroidble.internal;

import android.bluetooth.BluetoothDevice;

import com.jakewharton.rxrelay3.BehaviorRelay;
import io.nrbtech.rxandroidble.ClientComponent;
import io.nrbtech.rxandroidble.ClientComponent.NamedSchedulers;
import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.internal.connection.ConnectionComponent;
import io.nrbtech.rxandroidble.internal.connection.ConnectionStateChangeListener;
import io.nrbtech.rxandroidble.internal.connection.Connector;
import io.nrbtech.rxandroidble.internal.connection.ConnectorImpl;
import io.nrbtech.rxandroidble.internal.operations.TimeoutConfiguration;
import io.nrbtech.rxandroidble.internal.util.RxBleAdapterWrapper;

import java.util.concurrent.TimeUnit;

import bleshadow.dagger.Binds;
import bleshadow.dagger.Module;
import bleshadow.dagger.Provides;
import bleshadow.javax.inject.Named;
import io.reactivex.rxjava3.core.Scheduler;

@Module(subcomponents = ConnectionComponent.class)
public abstract class DeviceModule {

    public static final String MAC_ADDRESS = "mac-address";
    public static final String OPERATION_TIMEOUT = "operation-timeout";
    public static final String DISCONNECT_TIMEOUT = "disconnect-timeout";
    public static final String CONNECT_TIMEOUT = "connect-timeout";

    private static final int DEFAULT_OPERATION_TIMEOUT = 30;
    private static final int DEFAULT_DISCONNECT_TIMEOUT = 10;
    private static final int DEFAULT_CONNECT_TIMEOUT = 35;

    @Provides
    static BluetoothDevice provideBluetoothDevice(@Named(MAC_ADDRESS) String macAddress, RxBleAdapterWrapper adapterWrapper) {
        return adapterWrapper.getRemoteDevice(macAddress);
    }

    @Provides
    @Named(CONNECT_TIMEOUT)
    static TimeoutConfiguration providesConnectTimeoutConf(@Named(ClientComponent.NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler) {
        return new TimeoutConfiguration(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS, timeoutScheduler);
    }

    @Provides
    @Named(DISCONNECT_TIMEOUT)
    static TimeoutConfiguration providesDisconnectTimeoutConf(@Named(NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler) {
        return new TimeoutConfiguration(DEFAULT_DISCONNECT_TIMEOUT, TimeUnit.SECONDS, timeoutScheduler);
    }

    @Provides
    @DeviceScope
    static BehaviorRelay<RxBleConnection.RxBleConnectionState> provideConnectionStateRelay() {
        return BehaviorRelay.createDefault(RxBleConnection.RxBleConnectionState.DISCONNECTED);
    }

    @Provides
    @DeviceScope
    static ConnectionStateChangeListener provideConnectionStateChangeListener(
            final BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateBehaviorRelay
    ) {
        return new ConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                connectionStateBehaviorRelay.accept(rxBleConnectionState);
            }
        };
    }

    @Binds
    abstract Connector bindConnector(ConnectorImpl rxBleConnectionConnector);

    @Binds
    abstract RxBleDevice bindDevice(RxBleDeviceImpl rxBleDevice);
}

package io.nrbtech.rxandroidble.internal;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.Nullable;

import com.jakewharton.rxrelay3.BehaviorRelay;
import io.nrbtech.rxandroidble.ConnectionSetup;
import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.Timeout;
import io.nrbtech.rxandroidble.exceptions.BleAlreadyConnectedException;
import io.nrbtech.rxandroidble.exceptions.BleException;
import io.nrbtech.rxandroidble.exceptions.BlePermissionException;
import io.nrbtech.rxandroidble.internal.connection.Connector;

import io.nrbtech.rxandroidble.internal.logger.LoggerUtil;
import io.nrbtech.rxandroidble.internal.util.LocationServicesStatus;

import java.util.concurrent.atomic.AtomicBoolean;

import bleshadow.javax.inject.Inject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Supplier;

@DeviceScope
class RxBleDeviceImpl implements RxBleDevice {

    final BluetoothDevice bluetoothDevice;
    final Connector connector;
    private final BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay;
    private final LocationServicesStatus locationServicesStatus;
    final AtomicBoolean isConnected = new AtomicBoolean(false);

    @Inject
    RxBleDeviceImpl(
            BluetoothDevice bluetoothDevice,
            Connector connector,
            BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay,
            LocationServicesStatus locationServicesStatus
    ) {
        this.bluetoothDevice = bluetoothDevice;
        this.connector = connector;
        this.connectionStateRelay = connectionStateRelay;
        this.locationServicesStatus = locationServicesStatus;
    }

    @Override
    public Observable<RxBleConnection.RxBleConnectionState> observeConnectionStateChanges() {
        return connectionStateRelay.distinctUntilChanged().skip(1);
    }

    @Override
    public RxBleConnection.RxBleConnectionState getConnectionState() {
        return connectionStateRelay.getValue();
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect, final Timeout timeout) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setOperationTimeout(timeout)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    public Observable<RxBleConnection> establishConnection(final ConnectionSetup options) {
        return Observable.defer(new Supplier<ObservableSource<RxBleConnection>>() {
            @Override
            public ObservableSource<RxBleConnection> get() {
                if (!locationServicesStatus.isConnectPermissionOk()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        return Observable.error(new BlePermissionException(Manifest.permission.BLUETOOTH_CONNECT));
                    }
                    return Observable.error(new BleException("Unexpected connect permission not OK"));
                }
                if (isConnected.compareAndSet(false, true)) {
                    return connector.prepareConnection(options)
                            .doFinally(new Action() {
                                @Override
                                public void run() {
                                    isConnected.set(false);
                                }
                            });
                } else {
                    return Observable.error(new BleAlreadyConnectedException(bluetoothDevice.getAddress()));
                }
            }
        });
    }

    @Override
    @Nullable
    public String getName() {
        return bluetoothDevice.getName();
    }

    @Override
    public String getMacAddress() {
        return bluetoothDevice.getAddress();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RxBleDeviceImpl)) {
            return false;
        }

        RxBleDeviceImpl that = (RxBleDeviceImpl) o;
        return bluetoothDevice.equals(that.bluetoothDevice);
    }

    @Override
    public int hashCode() {
        return bluetoothDevice.hashCode();
    }

    @Override
    public String toString() {
        return "RxBleDeviceImpl{"
                + LoggerUtil.commonMacMessage(bluetoothDevice.getAddress())
                + ", name=" + bluetoothDevice.getName()
                + '}';
    }
}

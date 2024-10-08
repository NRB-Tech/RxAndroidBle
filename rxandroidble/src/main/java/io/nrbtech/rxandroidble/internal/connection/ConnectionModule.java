package io.nrbtech.rxandroidble.internal.connection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import io.nrbtech.rxandroidble.ClientComponent;
import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.Timeout;
import io.nrbtech.rxandroidble.internal.operations.OperationsProvider;
import io.nrbtech.rxandroidble.internal.operations.OperationsProviderImpl;
import io.nrbtech.rxandroidble.internal.operations.TimeoutConfiguration;
import io.nrbtech.rxandroidble.internal.serialization.ConnectionOperationQueue;
import io.nrbtech.rxandroidble.internal.serialization.ConnectionOperationQueueImpl;
import io.nrbtech.rxandroidble.internal.util.CharacteristicPropertiesParser;

import bleshadow.dagger.Binds;
import bleshadow.dagger.Module;
import bleshadow.dagger.Provides;
import bleshadow.dagger.multibindings.IntoSet;
import bleshadow.javax.inject.Named;
import bleshadow.javax.inject.Provider;
import io.reactivex.rxjava3.core.Scheduler;

import static io.nrbtech.rxandroidble.internal.connection.ConnectionComponent.NamedBooleans.SUPPRESS_OPERATION_CHECKS;
import static io.nrbtech.rxandroidble.internal.connection.ConnectionComponent.NamedInts.GATT_MTU_MINIMUM;
import static io.nrbtech.rxandroidble.internal.connection.ConnectionComponent.NamedInts.GATT_WRITE_MTU_OVERHEAD;

@Module
public abstract class ConnectionModule {

    public static final String OPERATION_TIMEOUT = "operation-timeout";

    @Provides
    @Named(OPERATION_TIMEOUT)
    static TimeoutConfiguration providesOperationTimeoutConf(
            @Named(ClientComponent.NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler,
            Timeout operationTimeout
    ) {
        return new TimeoutConfiguration(operationTimeout.timeout, operationTimeout.timeUnit, timeoutScheduler);
    }

    @Provides
    static IllegalOperationHandler provideIllegalOperationHandler(
            @Named(SUPPRESS_OPERATION_CHECKS) boolean suppressOperationCheck,
            Provider<LoggingIllegalOperationHandler> loggingIllegalOperationHandlerProvider,
            Provider<ThrowingIllegalOperationHandler> throwingIllegalOperationHandlerProvider
            ) {
        if (suppressOperationCheck) {
            return loggingIllegalOperationHandlerProvider.get();
        } else {
            return throwingIllegalOperationHandlerProvider.get();
        }
    }

    @Provides
    static CharacteristicPropertiesParser provideCharacteristicPropertiesParser() {
        return new CharacteristicPropertiesParser(BluetoothGattCharacteristic.PROPERTY_BROADCAST,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PROPERTY_INDICATE,
                BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE);
    }

    @Provides
    @Named(GATT_WRITE_MTU_OVERHEAD)
    static int gattWriteMtuOverhead() {
        return RxBleConnection.GATT_WRITE_MTU_OVERHEAD;
    }

    @Provides
    @Named(GATT_MTU_MINIMUM)
    static int minimumMtu() {
        return RxBleConnection.GATT_MTU_MINIMUM;
    }

    @Provides
    static BluetoothGatt provideBluetoothGatt(BluetoothGattProvider bluetoothGattProvider) {
        return bluetoothGattProvider.getBluetoothGatt();
    }

    @Binds
    abstract RxBleConnection.LongWriteOperationBuilder bindLongWriteOperationBuilder(LongWriteOperationBuilderImpl operationBuilder);

    @Binds
    abstract OperationsProvider bindOperationsProvider(OperationsProviderImpl operationsProvider);

    @Binds
    abstract MtuProvider bindCurrentMtuProvider(MtuWatcher mtuWatcher);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindMtuWatcherSubscriptionWatcher(MtuWatcher mtuWatcher);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindDisconnectActionSubscriptionWatcher(DisconnectAction disconnectAction);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindConnectionQueueSubscriptionWatcher(ConnectionOperationQueueImpl connectionOperationQueue);

    @Binds
    abstract RxBleConnection bindRxBleConnection(RxBleConnectionImpl rxBleConnection);

    @Binds
    abstract ConnectionOperationQueue bindConnectionOperationQueue(ConnectionOperationQueueImpl connectionOperationQueue);

    @Binds
    abstract DisconnectionRouterInput bindDisconnectionRouterInput(DisconnectionRouter disconnectionRouter);

    @Binds
    abstract DisconnectionRouterOutput bindDisconnectionRouterOutput(DisconnectionRouter disconnectionRouter);
}
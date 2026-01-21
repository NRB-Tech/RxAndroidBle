package io.nrbtech.rxandroidble.internal.operations;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import androidx.annotation.RequiresApi;

import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBlePhy;
import io.nrbtech.rxandroidble.RxBlePhyOption;
import io.nrbtech.rxandroidble.internal.connection.PayloadSizeLimitProvider;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface OperationsProvider {

    CharacteristicLongWriteOperation provideLongWriteOperation(
            BluetoothGattCharacteristic bluetoothGattCharacteristic,
            RxBleConnection.WriteOperationAckStrategy writeOperationAckStrategy,
            RxBleConnection.WriteOperationRetryStrategy writeOperationRetryStrategy,
            PayloadSizeLimitProvider maxBatchSizeProvider,
            byte[] bytes);

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    MtuRequestOperation provideMtuChangeOperation(int requestedMtu);

    CharacteristicReadOperation provideReadCharacteristic(BluetoothGattCharacteristic characteristic);

    DescriptorReadOperation provideReadDescriptor(BluetoothGattDescriptor descriptor);

    ReadRssiOperation provideRssiReadOperation();

    ServiceDiscoveryOperation provideServiceDiscoveryOperation(long timeout, TimeUnit timeUnit);

    CharacteristicWriteOperation provideWriteCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data);

    DescriptorWriteOperation provideWriteDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data);

    ConnectionPriorityChangeOperation provideConnectionPriorityChangeOperation(
            int connectionPriority,
            long delay,
            TimeUnit timeUnit
    );

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    PhyReadOperation providePhyReadOperation();

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    PhyUpdateOperation providePhyUpdateOperation(Set<RxBlePhy> txPhy, Set<RxBlePhy> rxPhy, RxBlePhyOption phyOptions);
}

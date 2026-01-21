package io.nrbtech.rxandroidble.internal.operations;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.nrbtech.rxandroidble.PhyPair;
import io.nrbtech.rxandroidble.RxBlePhyOption;
import io.nrbtech.rxandroidble.exceptions.BleGattOperationType;
import io.nrbtech.rxandroidble.internal.RxBlePhyImpl;
import io.nrbtech.rxandroidble.internal.SingleResponseOperation;
import io.nrbtech.rxandroidble.internal.connection.RxBleGattCallback;

import java.util.Set;

import bleshadow.javax.inject.Inject;
import io.reactivex.rxjava3.core.Single;

@RequiresApi(26 /* Build.VERSION_CODES.O */)
public class PhyUpdateOperation extends SingleResponseOperation<PhyPair> {

    Set<RxBlePhyImpl> txPhy;
    Set<RxBlePhyImpl> rxPhy;
    RxBlePhyOption phyOptions;

    @Inject
    PhyUpdateOperation(
            RxBleGattCallback rxBleGattCallback,
            BluetoothGatt bluetoothGatt,
            TimeoutConfiguration timeoutConfiguration,
            Set<RxBlePhyImpl> txPhy, Set<RxBlePhyImpl> rxPhy, RxBlePhyOption phyOptions) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.PHY_UPDATE, timeoutConfiguration);
        this.txPhy = txPhy;
        this.rxPhy = rxPhy;
        this.phyOptions = phyOptions;
    }

    @Override
    protected Single<PhyPair> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback.getOnPhyUpdate().firstOrError();
    }

    @Override
    @SuppressLint("MissingPermission")
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        bluetoothGatt.setPreferredPhy(
                RxBlePhyImpl.enumSetToValuesMask(txPhy),
                RxBlePhyImpl.enumSetToValuesMask(rxPhy),
                phyOptions.getValue()
        );
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhyUpdateOperation{"
                + super.toString()
                + ", txPhy=" + txPhy
                + ", rxPhy=" + rxPhy
                + ", phyOptions=" + phyOptions
                + '}';
    }
}

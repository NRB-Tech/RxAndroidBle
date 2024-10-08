package io.nrbtech.rxandroidble.exceptions;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.nrbtech.rxandroidble.internal.logger.LoggerUtil;
import io.nrbtech.rxandroidble.utils.GattStatusParser;

/**
 * Exception emitted when the BLE link has been interrupted as a result of an error. The exception contains
 * detailed explanation of the error source (type of operation) and the code proxied from
 * the <a href="https://cs.android.com/android/platform/superproject/+/master:packages/modules/Bluetooth/system/stack/include/gatt_api.h">
 * Android system</a>.
 *
 * @see io.nrbtech.rxandroidble.RxBleDevice#establishConnection(boolean)
 */
public class BleGattException extends BleException {

    public static final int UNKNOWN_STATUS = -1;

    @Nullable
    private final BluetoothGatt gatt;

    private final int status;
    private final BleGattOperationType bleGattOperationType;

    @Deprecated
    public BleGattException(int status, BleGattOperationType bleGattOperationType) {
        super(createMessage(null, status, bleGattOperationType));
        this.gatt = null;
        this.status = status;
        this.bleGattOperationType = bleGattOperationType;
    }

    public BleGattException(@NonNull BluetoothGatt gatt, int status, BleGattOperationType bleGattOperationType) {
        super(createMessage(gatt, status, bleGattOperationType));
        this.gatt = gatt;
        this.status = status;
        this.bleGattOperationType = bleGattOperationType;
    }

    public BleGattException(BluetoothGatt gatt, BleGattOperationType bleGattOperationType) {
        this(gatt, UNKNOWN_STATUS, bleGattOperationType);
    }

    public String getMacAddress() {
        return getMacAddress(gatt);
    }

    public BleGattOperationType getBleGattOperationType() {
        return bleGattOperationType;
    }

    public int getStatus() {
        return status;
    }

    private static String getMacAddress(@Nullable BluetoothGatt gatt) {
        return (gatt != null && gatt.getDevice() != null) ? gatt.getDevice().getAddress() : null;
    }

    @SuppressLint("DefaultLocale")
    private static String createMessage(@Nullable BluetoothGatt gatt, int status, BleGattOperationType bleGattOperationType) {
        if (status == UNKNOWN_STATUS) {
            return String.format("GATT exception from MAC address %s, with type %s",
                    getMacAddress(gatt), bleGattOperationType);
        }

        final String statusDescription = GattStatusParser.getGattCallbackStatusDescription(status);
        final String link
                = "https://cs.android.com/android/platform/superproject/+/master:packages/modules/Bluetooth/system/stack/include/"
                + "gatt_api.h";
        return String.format("GATT exception from %s, status %d (%s), type %s. (Look up status 0x%02x here %s)",
                LoggerUtil.commonMacMessage(gatt), status, statusDescription, bleGattOperationType, status, link);
    }
}

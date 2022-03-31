package io.nrbtech.rxandroidble.exceptions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import io.nrbtech.rxandroidble.NotificationSetupMode;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BleCannotSetCharacteristicNotificationException extends BleException {

    @IntDef({CANNOT_SET_LOCAL_NOTIFICATION, CANNOT_FIND_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR,
            CANNOT_WRITE_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Reason {

    }

    /**
     * Used when call to {@link android.bluetooth.BluetoothGatt#setCharacteristicNotification(BluetoothGattCharacteristic, boolean)}
     * returns false.
     */
    public static final int CANNOT_SET_LOCAL_NOTIFICATION = 1;

    /**
     * Used when a call to {@link BluetoothGattCharacteristic#getDescriptor(UUID)} does not return the Client Characteristic Configuration
     * Descriptor ("00002902-0000-1000-8000-00805f9b34fb"). This usually happens when there is a bug in the implementation of
     * the peripheral's firmware - according to Bluetooth Core Specification v4.2 [Vol 3, Part G] 3.3.1.1 every characteristic that has
     * {@link BluetoothGattCharacteristic#PROPERTY_NOTIFY} or {@link BluetoothGattCharacteristic#PROPERTY_INDICATE} shall contain
     * the Client Characteristic Configuration Descriptor. This is a violation of the specification and should be fixed on the firmware
     * side. As a temporary workaround
     * {@link io.nrbtech.rxandroidble.RxBleConnection#setupNotification(BluetoothGattCharacteristic, NotificationSetupMode)} or
     * {@link io.nrbtech.rxandroidble.RxBleConnection#setupIndication(UUID, NotificationSetupMode)} can be used with
     * {@link NotificationSetupMode#COMPAT} - which will make the library call only
     * {@link android.bluetooth.BluetoothGatt#setCharacteristicNotification(BluetoothGattCharacteristic, boolean)} without writing
     * the descriptor.
     */
    public static final int CANNOT_FIND_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = 2;

    /**
     * Used when a call to writing to Client Characteristic Configuration Descriptor fails due to
     * {@link BluetoothGatt#writeDescriptor(BluetoothGattDescriptor)} returns `false` or
     * {@link android.bluetooth.BluetoothGattCallback#onDescriptorWrite(BluetoothGatt, BluetoothGattDescriptor, int)} is called with status
     * other than {@link BluetoothGatt#GATT_SUCCESS}.
     */
    public static final int CANNOT_WRITE_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = 3;

    private final BluetoothGattCharacteristic bluetoothGattCharacteristic;

    @Reason
    private final int reason;

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public BleCannotSetCharacteristicNotificationException(BluetoothGattCharacteristic bluetoothGattCharacteristic, @Reason int reason,
                                                           Throwable cause) {
        super(createMessage(bluetoothGattCharacteristic, reason), cause);
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        this.reason = reason;
    }

    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return bluetoothGattCharacteristic;
    }

    /**
     * Should return one of {@link #CANNOT_SET_LOCAL_NOTIFICATION}, {@link #CANNOT_FIND_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR},
     * {@link #CANNOT_WRITE_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR}
     *
     * @return the reason
     */
    @Reason
    public int getReason() {
        return reason;
    }

    private static String createMessage(BluetoothGattCharacteristic bluetoothGattCharacteristic, @Reason int reason) {
        return reasonDescription(reason) + " (code "
                + reason + ") with characteristic UUID " + bluetoothGattCharacteristic.getUuid();
    }

    private static String reasonDescription(int reason) {
        switch (reason) {

            case CANNOT_FIND_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR:
                return "Cannot find client characteristic config descriptor";
            case CANNOT_WRITE_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR:
                return "Cannot write client characteristic config descriptor";
            case CANNOT_SET_LOCAL_NOTIFICATION:
                return "Cannot set local notification";
            default:
                return "Unknown error";
        }
    }
}

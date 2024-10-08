package io.nrbtech.rxandroidble.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;

import io.nrbtech.rxandroidble.internal.BluetoothGattCharacteristicProperty;
import io.nrbtech.rxandroidble.internal.logger.LoggerUtil;
import io.nrbtech.rxandroidble.internal.util.CharacteristicPropertiesParser;

import java.util.Locale;

import bleshadow.javax.inject.Inject;

public class IllegalOperationMessageCreator {

    private final CharacteristicPropertiesParser propertiesParser;

    @Inject
    public IllegalOperationMessageCreator(CharacteristicPropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    @SuppressWarnings("WrongConstant")
    public String createMismatchMessage(BluetoothGattCharacteristic characteristic,
                                        @BluetoothGattCharacteristicProperty int neededProperties) {
        return String.format(
                Locale.getDefault(),
                "Characteristic %s supports properties: %s (%d) does not have any property matching %s (%d)",
                LoggerUtil.getUuidToLog(characteristic.getUuid()),
                propertiesParser.propertiesIntToString(characteristic.getProperties()),
                characteristic.getProperties(),
                propertiesParser.propertiesIntToString(neededProperties),
                neededProperties
        );
    }
}

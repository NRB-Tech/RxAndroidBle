package io.nrbtech.rxandroidble.exceptions;

import java.util.UUID;

/**
 * An exception being emitted from {@link io.nrbtech.rxandroidble.RxBleDeviceServices#getCharacteristic(UUID)} or any
 * {@link io.nrbtech.rxandroidble.RxBleConnection} function that accepts {@link UUID} in case the said UUID is not found
 * in the discovered device services.
 */
public class BleCharacteristicNotFoundException extends BleException {

    private final UUID characteristicUUID;

    public BleCharacteristicNotFoundException(UUID characteristicUUID) {
        super("Characteristic not found with UUID " + characteristicUUID);
        this.characteristicUUID = characteristicUUID;
    }

    /**
     * Returns characteristic UUID that has not been found
     * @deprecated Use {@link #getCharacteristicUUID()}
     *
     * @return the UUID
     */
    @Deprecated
    public UUID getCharactersisticUUID() {
        return characteristicUUID;
    }

    /**
     * Returns characteristic UUID that has not been found
     *
     * @return the UUID
     */
    public UUID getCharacteristicUUID() {
        return characteristicUUID;
    }
}

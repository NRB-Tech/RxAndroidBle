package io.nrbtech.rxandroidble.exceptions;

import java.util.UUID;

/**
 * An exception being emitted from {@link io.nrbtech.rxandroidble.RxBleConnection#setupNotification(UUID)}/
 * {@link io.nrbtech.rxandroidble.RxBleConnection#setupIndication(UUID)} or overloads in case when an opposite
 * type (indication/notification) was already set.
 *
 * To make it possible to set this type of notification/indication the previous one must be unsubscribed.
 */
public class BleConflictingNotificationAlreadySetException extends BleException {

    private final UUID characteristicUuid;

    private final boolean alreadySetIsIndication;

    public BleConflictingNotificationAlreadySetException(UUID characteristicUuid, boolean alreadySetIsIndication) {
        super("Characteristic " + characteristicUuid
                + " notification already set to " + (alreadySetIsIndication ? "indication" : "notification"));
        this.characteristicUuid = characteristicUuid;
        this.alreadySetIsIndication = alreadySetIsIndication;
    }

    public UUID getCharacteristicUuid() {
        return characteristicUuid;
    }

    public boolean indicationAlreadySet() {
        return alreadySetIsIndication;
    }

    public boolean notificationAlreadySet() {
        return !alreadySetIsIndication;
    }
}

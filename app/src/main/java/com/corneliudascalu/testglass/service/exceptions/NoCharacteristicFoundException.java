package com.corneliudascalu.testglass.service.exceptions;

import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class NoCharacteristicFoundException extends Exception {

    private final UUID serviceUuid;

    private final UUID characteristicUuid;

    public NoCharacteristicFoundException(BluetoothGattService service, UUID characteristicUuid) {
        this.serviceUuid = service.getUuid();
        this.characteristicUuid = characteristicUuid;
    }

    public UUID getCharacteristicUuid() {
        return characteristicUuid;
    }

    public UUID getServiceUuid() {
        return serviceUuid;
    }
}

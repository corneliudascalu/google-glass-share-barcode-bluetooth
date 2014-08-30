package com.corneliudascalu.testglass.service.exceptions;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class NoServiceFoundException extends Exception {

    private final BluetoothDevice device;

    private final UUID uuid;

    public NoServiceFoundException(BluetoothDevice device, UUID uuid) {
        this.device = device;
        this.uuid = uuid;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public UUID getUuid() {
        return uuid;
    }
}

package com.corneliudascalu.testglass.service;

import android.bluetooth.BluetoothDevice;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface BluetoothClient {

    /**
     * Open a connection to a paired Bluetooth device. The connection will be kept alive as long as
     * possible
     */
    void connectToDevice(BluetoothDevice device);

    /**
     * Disconnect from a connected Bluetooth device
     */
    void disconnectFromDevice(BluetoothDevice device);

    /**
     * Add a message to the sending queue
     */
    void sendData(String data);

    /**
     * Set a callback to receive events from this client
     */
    void setCallback(ClientUiCallback callback);

}

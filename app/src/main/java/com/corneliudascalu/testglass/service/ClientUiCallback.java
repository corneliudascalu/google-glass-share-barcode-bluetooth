package com.corneliudascalu.testglass.service;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface ClientUiCallback {


    void onDeviceConnectionEstablished();

    void onDeviceConnectionFailed(Exception exception);

    void onDeviceDisconnected();

    void onMessageSent();

    void onMessageFailed();
}

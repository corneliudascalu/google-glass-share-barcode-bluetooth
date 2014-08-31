package com.corneliudascalu.testglass.service;

/**
* @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
*/
public class EmptyUiCallback implements ClientUiCallback {

    @Override
    public void onDeviceConnectionEstablished() {
    }

    @Override
    public void onDeviceConnectionFailed(Exception exception) {
    }

    @Override
    public void onDeviceDisconnected() {
    }

    @Override
    public void onMessageSent() {
    }

    @Override
    public void onMessageFailed() {
    }
}

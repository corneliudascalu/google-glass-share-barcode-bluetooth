package com.corneliudascalu.glass.phone.domain.message.gcm;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceUnsupportedStatusMessage extends GcmStatusMessage{

    public DeviceUnsupportedStatusMessage(int result, String message) {
        super(result, message);
    }
}

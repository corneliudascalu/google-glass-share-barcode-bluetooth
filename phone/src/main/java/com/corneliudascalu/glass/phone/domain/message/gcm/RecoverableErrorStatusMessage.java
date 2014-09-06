package com.corneliudascalu.glass.phone.domain.message.gcm;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RecoverableErrorStatusMessage extends GcmStatusMessage {

    public RecoverableErrorStatusMessage(int result, String message) {
        super(result, message);
    }
}

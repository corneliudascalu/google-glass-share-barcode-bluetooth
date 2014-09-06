package com.corneliudascalu.glass.phone.domain.message.gcm;

import com.corneliudascalu.glass.phone.domain.message.EventMessage;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public abstract class GcmStatusMessage implements EventMessage {

    private int result;

    private String message;

    public GcmStatusMessage(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

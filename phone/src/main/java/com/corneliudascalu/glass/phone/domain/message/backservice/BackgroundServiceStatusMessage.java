package com.corneliudascalu.glass.phone.domain.message.backservice;

import com.corneliudascalu.glass.phone.domain.message.EventMessage;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public abstract class BackgroundServiceStatusMessage implements EventMessage {

    private String message;

    public BackgroundServiceStatusMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

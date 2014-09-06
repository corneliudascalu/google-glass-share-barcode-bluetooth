package com.corneliudascalu.glass.phone.domain.message.backservice;

import java.io.IOException;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class ConnectionErrorMessage extends BackgroundServiceStatusMessage {

    private final IOException exception;

    public ConnectionErrorMessage(String message, IOException ex) {
        super(message);
        this.exception = ex;
    }

    public IOException getException() {
        return exception;
    }
}

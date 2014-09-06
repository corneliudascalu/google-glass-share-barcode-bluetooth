package com.corneliudascalu.glass.phone.domain.message.backservice;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class ConnectingToServerStatusMessage extends BackgroundServiceStatusMessage {

    private Status status;

    public ConnectingToServerStatusMessage(Status status, String message) {
        super(message);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        None,
        Failed,
        Success
    }
}

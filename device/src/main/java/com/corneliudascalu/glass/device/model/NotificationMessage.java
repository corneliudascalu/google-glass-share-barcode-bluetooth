package com.corneliudascalu.glass.device.model;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@osf-global.com>
 */
public class NotificationMessage {

    private String Message;

    private String title;

    public NotificationMessage(String title, String message) {
        this.title = title;
        this.Message = message;
    }

    public String getMessage() {
        return Message;
    }

    public String getTitle() {
        return title;
    }
}

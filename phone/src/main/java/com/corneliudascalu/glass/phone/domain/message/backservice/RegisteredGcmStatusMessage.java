package com.corneliudascalu.glass.phone.domain.message.backservice;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RegisteredGcmStatusMessage extends BackgroundServiceStatusMessage {

    private final String registrationId;

    public RegisteredGcmStatusMessage(String message, String registrationId) {
        super(message);
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}

package com.corneliudascalu.glass.phone.service;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class ServiceFsm {

    private State state = State.None;

    public State getState() {
        return state;
    }

    public void dontHaveActiveNetwork() {
        switch (state) {
            default:
                state = State.NoNetwork;
                break;
        }
    }

    public void cantConnectPlayServices() {
        state = State.PlayServicesNotConnected;
    }

    public void playServicesUnsupported() {
        state = State.PlayServicesUnavailable;
    }

    public void gotPlayServices() {
        switch (state) {
            case None:
            case PlayServicesNotConnected:
                state = State.PlayServicesConnected;
                break;
        }
    }

    public void gotGcmRegistrationId() {
        switch (state) {
            case RegisteredToServer:
                break;
            default:
                state = State.RegisteredToGcm;
                break;
        }
    }

    public void registeredServerSuccessfully() {
        switch (state) {
            default:
                state = State.RegisteredToServer;
                break;
        }
    }

    public void failedToRegisterServer() {
        switch (state) {
            case RegisteredToGcm:
                state = State.ServerRegistrationFailed;
                break;
            default:
                break;
        }
    }

    public enum State {
        None,
        NoNetwork,
        PlayServicesUnavailable,
        PlayServicesNotConnected,
        PlayServicesConnected,
        RegisteredToGcm,
        RegisteredToServer,
        ServerRegistrationFailed
    }

}

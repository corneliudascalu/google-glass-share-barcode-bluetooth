package com.corneliudascalu.glass.device.model;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@osf-global.com>
 */
public class DeviceMessage {

    private Device Device;

    private String Message;

    public static DeviceMessage create(Device device, String message) {
        DeviceMessage deviceMessage = new DeviceMessage();
        deviceMessage.setDevice(device);
        deviceMessage.setMessage(message);
        return deviceMessage;
    }

    public Device getDevice() {
        return Device;
    }

    public void setDevice(Device device) {
        this.Device = device;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}

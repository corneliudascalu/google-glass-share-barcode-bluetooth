package com.corneliudascalu.glass.device.model;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class Device {

    public static final String SELECTED_DEVICE_KEY = "selectedDevice" + Device.class.getName();

    public static final String PREFERENCES_NAME = "prefsName" + Device.class.getName();

    public static final Device NO_DEVICE = new Device("noname", "notoken", "nouuid");

    private String name;

    private String token;

    private int ostype = 2;

    private String uuid;

    public Device() {
        name = RandomStringUtils.randomAlphabetic(7);
        token = RandomStringUtils.randomAlphanumeric(20);
    }

    public Device(String name, String token, String uuid) {
        this.name = name;
        this.token = token;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

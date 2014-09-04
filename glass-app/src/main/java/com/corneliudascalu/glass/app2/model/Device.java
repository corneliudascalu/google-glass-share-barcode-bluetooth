package com.corneliudascalu.glass.app2.model;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class Device {

    public static final String SELECTED_DEVICE_KEY = "selectedDevice" + Device.class.getName();

    public static final String PREFERENCES_NAME = "prefsName" + Device.class.getName();

    public static final Device NO_DEVICE = new Device("noname", "notoken");

    private String name;

    private String token;

    public Device() {
        name = RandomStringUtils.randomAlphabetic(7);
        token = RandomStringUtils.randomAlphanumeric(20);
    }

    public Device(String name, String token) {
        this.name = name;
        this.token = token;
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
}

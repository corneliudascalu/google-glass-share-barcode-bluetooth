package com.corneliudascalu.glass.device.data;

import com.corneliudascalu.glass.device.model.Device;

import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface DeviceRepository {

    List<Device> getDevices() throws IOException;

    boolean sendData(Device device, String data) throws IOException;

    boolean registerToServer(Device device) throws IOException;

    void saveSelectedDevice(Context context, Device device);

    Device getSelectedDevice(Context context);
}

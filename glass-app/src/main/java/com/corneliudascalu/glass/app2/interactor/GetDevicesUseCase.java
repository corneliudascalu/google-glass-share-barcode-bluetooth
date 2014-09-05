package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.device.model.Device;

import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface GetDevicesUseCase {

    void execute(Callback callback);

    interface Callback {

        void onDevicesLoaded(List<Device> devices);

        void onLoadDeviceListError(Throwable error);
    }
}

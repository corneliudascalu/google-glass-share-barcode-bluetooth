package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.device.model.Device;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface SelectDeviceUseCase {

    void execute(Device device, Callback callback);

    interface Callback{

        void onDeviceSelected();

        void onDeviceSelectError(Throwable error);
    }
}

package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.device.model.Device;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface GetSelectedDeviceUseCase {

    void execute(Callback callback);

    interface Callback {

        void onDeviceFound(Device device);

        void onGetSelectedDeviceError(Throwable throwable);
    }
}

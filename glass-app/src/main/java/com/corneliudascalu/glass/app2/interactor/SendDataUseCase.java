package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.model.Device;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface SendDataUseCase {

    void execute(Device device, String data);

    interface Callback{

        void onDataSent();

        void onSendFailed(Throwable throwable);
    }
}

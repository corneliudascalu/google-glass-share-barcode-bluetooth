package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.model.Device;

import android.os.AsyncTask;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RoughSelectDeviceUseCase extends AsyncTask<Device, Void, Boolean>
        implements SelectDeviceUseCase {

    private Callback callback;

    @Override
    public void execute(Device device, Callback callback) {
        this.callback = callback;
        execute(device);
    }

    @Override
    protected Boolean doInBackground(Device... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (callback != null) {
            callback.onDeviceSelected();
        }
    }
}

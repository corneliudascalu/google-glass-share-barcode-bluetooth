package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.model.Device;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RoughGetDevicesUseCase extends AsyncTask<Void, Void, List<Device>>
        implements GetDevicesUseCase {

    private Callback callback;

    @Override
    public void execute(Callback callback) {
        this.callback = callback;
        execute();
    }

    @Override
    protected List<Device> doInBackground(Void... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Device> devices = new ArrayList<Device>(10);
        for (int i = 0; i < 10; i++) {
            devices.add(new Device());
        }

        return devices;
    }

    @Override
    protected void onPostExecute(List<Device> devices) {
        super.onPostExecute(devices);
        if (callback != null) {
            callback.onDevicesLoaded(devices);
        }
    }


}

package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RoughGetDevicesUseCase extends AsyncTask<Void, Void, List<Device>>
        implements GetDevicesUseCase {

    private Callback callback;

    private DeviceRepository deviceRepository;

    public RoughGetDevicesUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void execute(Callback callback) {
        this.callback = callback;
        execute();
    }

    @Override
    protected List<Device> doInBackground(Void... params) {
        try {
            return deviceRepository.getDevices();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Device> devices) {
        super.onPostExecute(devices);
        if (callback != null) {
            callback.onDevicesLoaded(devices);
        }
    }


}

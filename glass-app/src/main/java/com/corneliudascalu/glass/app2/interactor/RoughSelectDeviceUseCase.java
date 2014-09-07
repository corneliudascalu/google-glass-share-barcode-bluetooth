package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.GlassApp;
import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.os.AsyncTask;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RoughSelectDeviceUseCase extends AsyncTask<Device, Void, Boolean>
        implements SelectDeviceUseCase {

    private final DeviceRepository repository;

    private Callback callback;

    public RoughSelectDeviceUseCase(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Device device, Callback callback) {
        this.callback = callback;
        execute(device);
    }

    @Override
    protected Boolean doInBackground(Device... params) {
        repository.saveSelectedDevice(GlassApp.getInstance(), params[0]);

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

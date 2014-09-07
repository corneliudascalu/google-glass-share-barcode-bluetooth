package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.GlassApp;
import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.os.AsyncTask;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class GetSelectedDeviceUseCaseImpl extends AsyncTask<Void, Void, Device>
        implements GetSelectedDeviceUseCase {

    private final DeviceRepository repository;

    public GetSelectedDeviceUseCaseImpl(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Callback callback) {
        execute();
    }

    @Override
    protected Device doInBackground(Void... params) {
        return repository.getSelectedDevice(GlassApp.getInstance());
    }
}

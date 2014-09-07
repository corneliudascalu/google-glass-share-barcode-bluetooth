package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class RoughGetDevicesUseCase
        extends AsyncTask<Void, Void, RoughGetDevicesUseCase.GetDevicesResult>
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
    protected GetDevicesResult doInBackground(Void... params) {
        try {
            return new GetDevicesResult(deviceRepository.getDevices());
        } catch (IOException e) {
            return new GetDevicesResult(e);
        }
    }

    @Override
    protected void onPostExecute(GetDevicesResult result) {
        super.onPostExecute(result);
        if (callback != null) {
            if (result.isSuccessful()) {
                callback.onDevicesLoaded(result.getResult());
            } else {
                callback.onLoadDeviceListError(result.getError());
            }
        }
    }

    class GetDevicesResult implements RequestResult<List<Device>> {

        private final boolean success;

        public GetDevicesResult(List<Device> devices) {
            result = devices;
            success = true;
        }

        public GetDevicesResult(Exception e) {
            exception = e;
            success = false;
        }

        private List<Device> result;

        private Exception exception;

        @Override
        public List<Device> getResult() {
            return result;
        }

        @Override
        public Exception getError() {
            return exception;
        }

        @Override
        public boolean isSuccessful() {
            return success;
        }
    }

}

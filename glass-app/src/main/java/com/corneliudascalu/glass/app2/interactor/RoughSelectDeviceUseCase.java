package com.corneliudascalu.glass.app2.interactor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.corneliudascalu.glass.app2.GlassApp;
import com.corneliudascalu.glass.device.model.Device;

import android.content.Context;
import android.content.SharedPreferences;
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
        Gson gson = new GsonBuilder().create();
        String deviceJson = gson.toJson(params[0]);
        SharedPreferences prefs = GlassApp.getInstance()
                .getSharedPreferences(Device.PREFERENCES_NAME,Context.MODE_PRIVATE);
        prefs.edit().putString(Device.SELECTED_DEVICE_KEY, deviceJson).apply();

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

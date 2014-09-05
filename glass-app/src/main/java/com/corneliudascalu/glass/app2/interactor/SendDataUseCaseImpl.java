package com.corneliudascalu.glass.app2.interactor;

import com.corneliudascalu.glass.app2.GlassApp;
import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class SendDataUseCaseImpl extends AsyncTask<Pair<String, Device>, Void, Throwable>
        implements SendDataUseCase {

    @Override
    public void execute(Device device, String data) {
        execute(new Pair<String, Device>(data, device));
    }

    @Override
    protected Throwable doInBackground(Pair<String, Device>... params) {
        try {
            boolean b = new DeviceRepository().sendData(params[0].second, params[0].first);
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        super.onPostExecute(throwable);
        if (throwable != null) {
            Toast.makeText(GlassApp.getInstance().getApplicationContext(),
                    "Failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(GlassApp.getInstance().getApplicationContext(), "Success",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

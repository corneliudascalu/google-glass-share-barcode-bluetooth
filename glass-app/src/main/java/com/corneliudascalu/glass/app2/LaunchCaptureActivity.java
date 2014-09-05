package com.corneliudascalu.glass.app2;

import com.google.android.glass.app.Card;
import com.google.gson.GsonBuilder;

import com.corneliudascalu.glass.device.model.Device;
import com.github.barcodeeye.scan.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class LaunchCaptureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());

        Device selectedDevice = getSelectedDevice();
        if (selectedDevice.equals(Device.NO_DEVICE)) {
            startActivity(new Intent(this, SelectDeviceActivity.class));
            Toast.makeText(this, "Please connect to a device", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Connected to " + selectedDevice.getName(), Toast.LENGTH_SHORT)
                    .show();
            startActivity(CaptureActivity.newIntent(this));
        }

    }

    private Device getSelectedDevice() {
        SharedPreferences prefs = getSharedPreferences(Device.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        String deviceJson = prefs.getString(Device.SELECTED_DEVICE_KEY, null);
        if (deviceJson == null) {
            return Device.NO_DEVICE;
        } else {
            return new GsonBuilder().create().fromJson(deviceJson, Device.class);
        }
    }

    private View buildView() {
        Card card = new Card(this);

        card.setText("Launching capture...");
        return card.getView();

    }
}

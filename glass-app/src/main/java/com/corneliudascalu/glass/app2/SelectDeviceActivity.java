package com.corneliudascalu.glass.app2;

import com.google.android.glass.widget.CardScrollView;

import com.corneliudascalu.glass.app2.model.Device;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class SelectDeviceActivity extends Activity {

    private DeviceCardAdapter adapter;

    private CardScrollView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
        view.postDelayed(new Runnable() {
            ArrayList<Device> devices = new ArrayList<Device>(10);

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    devices.add(new Device());
                }
                adapter.setDevices(SelectDeviceActivity.this, devices);
            }
        }, 2000);
    }

    private View buildView() {
        adapter = new DeviceCardAdapter();
        view = new CardScrollView(this);
        view.setAdapter(adapter);
        return view;
    }
}

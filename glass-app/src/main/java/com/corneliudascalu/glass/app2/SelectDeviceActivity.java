package com.corneliudascalu.glass.app2;

import com.google.android.glass.widget.CardScrollView;

import com.corneliudascalu.glass.app2.model.Device;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Selected " + ((Device) adapter.getItem(position)).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.activate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.deactivate();
    }

    private View buildView() {
        adapter = new DeviceCardAdapter(this);
        view = new CardScrollView(this);
        view.setAdapter(adapter);
        return view;
    }
}

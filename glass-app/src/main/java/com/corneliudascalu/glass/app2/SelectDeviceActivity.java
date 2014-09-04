package com.corneliudascalu.glass.app2;

import com.google.android.glass.widget.CardScrollView;

import com.corneliudascalu.glass.app2.model.Device;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectDeviceActivity extends Activity {


    private DeviceCardAdapter adapter;

    @InjectView(R.id.deviceScroller)
    CardScrollView view;

    @InjectView(R.id.deviceProgressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_device);
        ButterKnife.inject(this, this);
        adapter = new DeviceCardAdapter(this);
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Selected " + ((Device) adapter.getItem(position)).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        view.postDelayed(new Runnable() {
            ArrayList<Device> devices = new ArrayList<Device>(10);

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < 10; i++) {
                    devices.add(new Device());
                }
                adapter.setDevices(SelectDeviceActivity.this, devices);
                view.setVisibility(View.VISIBLE);
            }
        }, 2000);
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
}

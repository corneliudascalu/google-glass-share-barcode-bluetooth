package com.corneliudascalu.glass.app2;

import com.google.android.glass.widget.CardScrollView;

import com.corneliudascalu.glass.app2.interactor.GetDevicesUseCase;
import com.corneliudascalu.glass.app2.interactor.RoughGetDevicesUseCase;
import com.corneliudascalu.glass.app2.interactor.RoughSelectDeviceUseCase;
import com.corneliudascalu.glass.app2.interactor.SelectDeviceUseCase;
import com.corneliudascalu.glass.device.model.Device;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectDeviceActivity extends Activity implements GetDevicesUseCase.Callback,
        SelectDeviceUseCase.Callback {


    private DeviceCardAdapter adapter;

    @InjectView(R.id.deviceScroller)
    CardScrollView cardScrollView;

    @InjectView(R.id.deviceProgressBar)
    ProgressBar progressBar;

    private GetDevicesUseCase getDevicesUseCase = new RoughGetDevicesUseCase();

    private SelectDeviceUseCase selectDeviceUseCase = new RoughSelectDeviceUseCase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_device);
        ButterKnife.inject(this, this);
        adapter = new DeviceCardAdapter(this);
        cardScrollView.setAdapter(adapter);

        cardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = (Device) adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "Selecting " + device.getName(),
                        Toast.LENGTH_SHORT).show();
                selectDevice(device);
            }

        });

        getDevicesUseCase.execute(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardScrollView.activate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cardScrollView.deactivate();
    }

    private void selectDevice(Device device) {
        progressBar.setVisibility(View.VISIBLE);
        cardScrollView.setVisibility(View.GONE);
        selectDeviceUseCase.execute(device, this);
    }

    @Override
    public void onDevicesLoaded(List<Device> devices) {
        progressBar.setVisibility(View.GONE);
        cardScrollView.setVisibility(View.VISIBLE);
        adapter.setDevices(this, devices);
    }

    @Override
    public void onLoadDeviceListError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceSelected() {
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDeviceSelectError(Throwable error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

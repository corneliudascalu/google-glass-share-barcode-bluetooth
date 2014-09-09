package com.corneliudascalu.glass.app2;

import com.google.android.glass.app.Card;

import com.corneliudascalu.glass.app2.interactor.GetSelectedDeviceUseCase;
import com.corneliudascalu.glass.app2.interactor.GetSelectedDeviceUseCaseImpl;
import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.data.DeviceRepositoryImpl;
import com.corneliudascalu.glass.device.model.Device;
import com.github.barcodeeye.scan.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class LaunchCaptureActivity extends Activity implements GetSelectedDeviceUseCase.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        DeviceRepository repository = new DeviceRepositoryImpl();
        new GetSelectedDeviceUseCaseImpl(repository).execute(this);
    }

    private View buildView() {
        Card card = new Card(this);

        card.setText("Launching capture...");
        return card.getView();

    }

    @Override
    public void onDeviceFound(Device device) {
        if (device.equals(Device.NO_DEVICE)) {
            Toast.makeText(this, "Please connect to a device", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SelectDeviceActivity.class));
        } else {
            Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT)
                    .show();
            startActivity(CaptureActivity.newIntent(this));
        }
    }

    @Override
    public void onGetSelectedDeviceError(Throwable throwable) {
        Toast.makeText(this, "Please connect to a device", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SelectDeviceActivity.class));
    }
}

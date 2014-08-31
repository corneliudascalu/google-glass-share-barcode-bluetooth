package com.corneliudascalu.testglass;

import com.corneliudascalu.testglass.service.BluetoothInterface;
import com.corneliudascalu.testglass.service.BluetoothService;
import com.corneliudascalu.testglass.service.ClientUiCallback;
import com.corneliudascalu.testglass.service.GattServerService;
import com.corneliudascalu.testglass.service.ILocalBinder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class BluetoothConnectedActivity extends Activity implements ClientUiCallback {

    public static final String EXTRA_DEVICES = "devices";

    public static final String TAG = "BluetoothActivity";

    private BluetoothInterface bluetoothInterface;

    private boolean bound;

    private BluetoothAdapter bluetoothAdapter;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            bluetoothInterface = ((ILocalBinder) service).getService();
            bluetoothInterface.setCallback(BluetoothConnectedActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBluetooth();
    }

    protected void checkBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // this is Google Glass, it should have bluetooth enabled at all times
        Log.d(MainActivity.TAG, "Bluetooth already enabled");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice bluetoothDevice = bondedDevices.iterator().next();
        startCommunicationService(bluetoothDevice);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    public void sendData(String data) {
        bluetoothInterface.sendData(data);
    }

    private void startCommunicationService(BluetoothDevice bluetoothDevice) {
        Intent intent;
        switch (bluetoothDevice.getType()) {
            case BluetoothDevice.DEVICE_TYPE_LE:
                intent = new Intent(this, GattServerService.class);
                break;
            default:
                intent = new Intent(this, BluetoothService.class);
                break;
        }
        intent.putExtra(BluetoothInterface.EXTRA_DEVICE, bluetoothDevice);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDeviceConnectionEstablished() {
        Log.d(TAG, "Connected to device");
        Toast.makeText(this, "Connected to device", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceConnectionFailed(Exception exception) {
        Log.e(TAG, "Failed to connect to device", exception);
        Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceDisconnected() {
        Log.d(TAG, "Disconnected from device");
        Toast.makeText(this, "Disconnected from device", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageSent() {
        Log.d(TAG, "Message sent successfully");
        Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageFailed() {
        Log.d(TAG, "Message failed");
        Toast.makeText(this, "Message failed. Please try again", Toast.LENGTH_SHORT).show();
    }
}

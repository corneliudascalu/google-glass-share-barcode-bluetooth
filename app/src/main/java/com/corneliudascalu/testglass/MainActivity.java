package com.corneliudascalu.testglass;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import com.corneliudascalu.testglass.service.BluetoothInterface;
import com.corneliudascalu.testglass.service.BluetoothService;
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
import android.view.View;

import java.util.Set;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    public static final String TAG = "Main";

    public static final String EXTRA_DEVICES = "devices";

    private BluetoothInterface bluetoothInterface;

    private boolean bound;

    private View cardView;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(buildView());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // this is Google Glass, it should have bluetooth enabled at all times
        Log.d(TAG, "Bluetooth already enabled");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice bluetoothDevice = bondedDevices.iterator().next();
        startCommunicationService(bluetoothDevice);

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
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardView.requestFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link Card} class.
     */
    private View buildView() {
        Card card = new Card(this);

        card.setText(R.string.hello_world);
        cardView = card.getView();

        return cardView;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            bluetoothInterface = ((ILocalBinder) service).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
}

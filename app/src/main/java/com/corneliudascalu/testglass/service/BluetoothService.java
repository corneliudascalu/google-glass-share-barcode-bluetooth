package com.corneliudascalu.testglass.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class BluetoothService extends Service {

    public static final String TAG = "BT_SERVICE";

    public static final UUID BLUETOOTH_UUID = UUID
            .fromString("e64cb46c-d317-467c-ae58-4b4b461f1e04");

    public static final int MSG_CONNECTED = 1;

    public static final int MSG_ERROR_CONNECTING = 2;

    public static final int MSG_DATA_READ = 3;

    public static final int MSG_SEND_DATA = 4;

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";

    private BluetoothAdapter bluetoothAdapter;

    private Handler handler;

    private Handler workHandler;

    private LocalBinder binder = new LocalBinder();

    private WorkThread workThread;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new ServiceHandler(Looper.getMainLooper());
    }

    public static Intent createStartIntent(Context context, BluetoothDevice device) {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(EXTRA_DEVICE, device);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        if (device != null) {
            startConnectThread(device);
        } else {
            Log.e(TAG, "No device in the start command");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void connectToDevice(BluetoothDevice device) {
        startConnectThread(device);
    }

    public void sendData(String data) {
        if (workHandler != null) {
            Message msg = Message.obtain();
            msg.what = MSG_SEND_DATA;
            msg.obj = data;
            workHandler.sendMessage(msg);
        }
    }

    private void startConnectThread(BluetoothDevice device) {
        ConnectThread connectThread = new ConnectThread(device);
        connectThread.start();
    }

    private void startWritingThread(BluetoothSocket socket) {
        workThread = new WorkThread(socket);
        workThread.start();
        workHandler = new ServiceHandler(workThread.getLooper());
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;

        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to socket,
            // because socket is final
            BluetoothSocket tmp = null;
            this.device = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Failed to create socket", e);
            }
            socket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                socket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.e(TAG, "Failed to connect", connectException);
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Failed to close socket", closeException);
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            // startWritingThread(socket);
            handler.obtainMessage(MSG_CONNECTED, socket).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private class WorkThread extends HandlerThread {

        private final BluetoothSocket socket;

        private final InputStream inStream;

        private final OutputStream outStream;

        public WorkThread(BluetoothSocket socket) {
            super("WorkThread", Process.THREAD_PRIORITY_BACKGROUND);
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    handler.obtainMessage(MSG_DATA_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                outStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    class ServiceHandler extends Handler {


        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CONNECTED:
                    BluetoothSocket socket = (BluetoothSocket) msg.obj;
                    if (socket != null) {
                        Log.d(TAG, "Connected successfully to phone");
                        Toast.makeText(getApplicationContext(), "Connected successfully to phone",
                                Toast.LENGTH_SHORT).show();
                        startWritingThread(socket);
                    } else {
                        Log.e(TAG, "Failed to connect to phone");
                    }
                    break;
                case MSG_SEND_DATA:
                    String data = (String) msg.obj;
                    workThread.write(data.getBytes());
                    break;
            }
        }
    }

    public class LocalBinder extends Binder {

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
}

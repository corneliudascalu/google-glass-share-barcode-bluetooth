package com.corneliudascalu.testglass.service;

import com.corneliudascalu.testglass.service.exceptions.ConnectSocketException;
import com.corneliudascalu.testglass.service.exceptions.CreateSocketException;
import com.corneliudascalu.testglass.service.exceptions.NullSocketException;
import com.corneliudascalu.testglass.service.exceptions.OpenStreamException;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class BluetoothService extends Service implements BluetoothInterface, Handler.Callback {

    public static final String TAG = "BT_SERVICE";

    public static final UUID BLUETOOTH_UUID = UUID
            .fromString("E20A39F4-73F5-4BC4-A12F-17D1AD07A961");

    public static final int MSG_CONNECTED = 1;

    public static final int MSG_ERROR_CONNECTING = 2;

    public static final int MSG_DATA_READ = 3;

    public static final int MSG_SEND_DATA = 4;

    public static final int RETRY_LIMIT = 3;

    public static final int RECREATE_SOCKET_RETRIES = 2;


    private Handler handler;

    private Handler workHandler;

    private LocalBinder binder = new LocalBinder();

    private BluetoothSocket socket;

    private ClientUiCallback clientCallback;

    public static final long INITIAL_RETRY_DELAY = 1000;

    private ConcurrentLinkedQueue<String> queue;

    private BluetoothDevice device;

    private int recreateSocketRetries = RECREATE_SOCKET_RETRIES;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper(), this);

        HandlerThread workThread = new HandlerThread("WorkThread");
        workThread.start();
        workHandler = new Handler(workThread.getLooper(), this);
        queue = new ConcurrentLinkedQueue<String>();
        clientCallback = new EmptyUiCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothInterface.EXTRA_DEVICE);
            if (device != null) {
                connectToDevice(device);
            } else {
                Log.e(TAG, "No device in the start command");
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public void setCallback(ClientUiCallback callback) {
        this.clientCallback = callback;
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        workHandler.obtainMessage(BtMessage.Connect.ordinal(), device).sendToTarget();
    }

    @Override
    public void disconnectFromDevice(BluetoothDevice device) {
        workHandler.obtainMessage(BtMessage.Disconnect.ordinal(), device).sendToTarget();
    }

    @Override
    public void sendData(String data) {
        workHandler.obtainMessage(BtMessage.Send.ordinal(), data).sendToTarget();
    }

    private void openConnectionToServer(BluetoothDevice device) {
        try {
            if (socket == null) {
                socket = createBtSocket(device);
            }
            if (!socket.isConnected()) {
                connectSocket(socket);
            }
            handler.obtainMessage(BtMessage.Connected.ordinal()).sendToTarget();
            checkQueue();
        } catch (CreateSocketException e) {
            handler.obtainMessage(BtMessage.ConnectionError.ordinal(), e).sendToTarget();
        } catch (ConnectSocketException e) {
            retrySocketConnectionBackoffFibonacci();
        }

    }

    private BluetoothSocket createBtSocket(BluetoothDevice device) throws CreateSocketException {
        try {
            return device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
        } catch (IOException e) {
            throw new CreateSocketException(e);
        }
    }

    private void connectSocket(BluetoothSocket socket) throws ConnectSocketException {
        try {
            socket.connect();
        } catch (IOException e) {
            throw new ConnectSocketException(e);
        }
    }

    private void retrySocketConnectionBackoffFibonacci() {
        // initiate backoff retries using the Fibonacci sequence
        int retries = RETRY_LIMIT;
        long previousDelay = INITIAL_RETRY_DELAY;
        long delay = INITIAL_RETRY_DELAY;
        while (retries >= 0) {
            try {
                Thread.sleep(delay);
                Log.d(TAG, "Reconnecting socket. Retries left: " + retries);
                connectSocket(socket);
                retries = RETRY_LIMIT;
                recreateSocketRetries = RECREATE_SOCKET_RETRIES;
                handler.obtainMessage(BtMessage.Connected.ordinal()).sendToTarget();
                checkQueue();
            } catch (ConnectSocketException e) {
                Log.d(TAG, "Reconnecting failed");
                retries--;
                delay = previousDelay + delay;
                previousDelay = delay - previousDelay;
                if (retries == -1) {
                    if (recreateSocketRetries > 0) {
                        Log.d(TAG, "Recreating socket. Retries left: " + recreateSocketRetries);
                        recreateSocketRetries--;
                        retries = RETRY_LIMIT;
                        closeSocket();
                        openConnectionToServer(device);
                    } else {
                        // abandon ship
                        handler.obtainMessage(BtMessage.ConnectionError.ordinal(), e)
                                .sendToTarget();
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private boolean writeDataToSocket(String data) {
        try {
            OutputStream outputStream = getSocketOutputStream();
            writeToStream(outputStream, data);
            handler.obtainMessage(BtMessage.Success.ordinal()).sendToTarget();
            return true;
        } catch (OpenStreamException e) {
            handler.obtainMessage(BtMessage.ConnectionError.ordinal(), e).sendToTarget();
            return false;
        } catch (NullSocketException e) {
            // socket destroyed, probably disconnected intentionally
            return false;
        } catch (ConnectSocketException e) {
            //retry socket connection
            retrySocketConnectionBackoffFibonacci();
            return false;
        }
    }

    private OutputStream getSocketOutputStream() throws OpenStreamException, NullSocketException {
        if (socket != null) {
            try {
                return socket.getOutputStream();
            } catch (IOException e) {
                throw new OpenStreamException(e);
            }
        } else {
            throw new NullSocketException(new IOException("Socket is null"));
        }
    }

    private void writeToStream(OutputStream stream, String string) throws ConnectSocketException {
        try {
            stream.write(string.getBytes());
        } catch (IOException e) {
            //thrown if the socket is not open
            throw new ConnectSocketException(e);
        }
    }

    private void checkQueue() {
        String queuedMessage = queue.peek();
        if (queuedMessage != null) {
            boolean success = writeDataToSocket(queuedMessage);
            if (success) {
                queue.poll();
                checkQueue();
            }
        }
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
            }
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        BtMessage message = BtMessage.valueOf(msg.what);
        switch (message) {

            case Connect:
                device = (BluetoothDevice) msg.obj;
                openConnectionToServer(device);
                break;
            case Disconnect:
                closeSocket();
                break;
            case Send:
                String data = (String) msg.obj;
                queue.add(data);
                checkQueue();
                break;
            case Connected:
                clientCallback.onDeviceConnectionEstablished();
                break;
            case ConnectionError:
                Exception exception = (Exception) msg.obj;
                clientCallback.onDeviceConnectionFailed(exception);
                break;
            case Disconnected:
                clientCallback.onDeviceDisconnected();
                break;
            case Success:
                clientCallback.onMessageSent();
                break;
            case SendError:
                clientCallback.onMessageFailed();
                break;
        }
        return true;
    }

    public class LocalBinder extends Binder implements ILocalBinder {

        public BluetoothInterface getService() {
            return BluetoothService.this;
        }
    }

    public static enum BtMessage {
        Connect,
        Disconnect,
        Send,
        Connected,
        ConnectionError,
        Disconnected,
        Success,
        SendError;

        public static BtMessage valueOf(int what) {
            if (what < values().length) {
                return values()[what];
            }
            return null;
        }
    }

}

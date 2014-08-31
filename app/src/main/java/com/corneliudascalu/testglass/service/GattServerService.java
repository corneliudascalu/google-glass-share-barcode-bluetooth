package com.corneliudascalu.testglass.service;

import com.corneliudascalu.testglass.service.exceptions.GattExploreException;
import com.corneliudascalu.testglass.service.exceptions.NoCharacteristicFoundException;
import com.corneliudascalu.testglass.service.exceptions.NoServiceFoundException;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.UUID;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class GattServerService extends Service implements BluetoothInterface {

    private static final UUID SERVICE_UUID = UUID
            .fromString("E20A39F4-73F5-4BC4-A12F-17D1AD07A961");

    private static final UUID CHARACTERISTIC_UUID = UUID.fromString(
            "08590F7E-DB05-467E-8757-72F6FAEB13D4");

    private ServerBinder serverBinder;

    private BluetoothGattCallback gattCallback;

    private BluetoothGatt gatt;

    private ClientUiCallback clientUiCallback;

    private BluetoothGattCharacteristic characteristic;

    @Override
    public void onCreate() {
        super.onCreate();
        serverBinder = new ServerBinder();
        clientUiCallback = new EmptyUiCallback();
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt _gatt, int status,
                    int newState) {
                super.onConnectionStateChange(_gatt, status, newState);
                gatt = _gatt;
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        clientUiCallback.onDeviceDisconnected();
                        break;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (BluetoothGatt.GATT_SUCCESS == status) {
                    setUpDataConnection();
                } else {
                    clientUiCallback.onDeviceConnectionFailed(new GattExploreException());
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if (BluetoothGatt.GATT_SUCCESS == status) {
                    clientUiCallback.onMessageSent();
                } else {
                    clientUiCallback.onMessageFailed();
                }
            }

        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        serverBinder = new ServerBinder();
        return serverBinder;
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        device.connectGatt(this, false, gattCallback);
    }

    @Override
    public void disconnectFromDevice(BluetoothDevice device) {
        gatt.disconnect();
    }

    @Override
    public void sendData(String data) {
        writeCharacteristic(data);
    }

    @Override
    public void setCallback(ClientUiCallback callback) {
        clientUiCallback = callback;
    }

    private void setUpDataConnection() {
        try {
            BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if (service == null) {
                throw new NoServiceFoundException(gatt.getDevice(), SERVICE_UUID);
            }
            characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
            if (characteristic == null) {
                throw new NoCharacteristicFoundException(service, CHARACTERISTIC_UUID);
            }
            clientUiCallback.onDeviceConnectionEstablished();
        } catch (Exception e) {
            clientUiCallback.onDeviceConnectionFailed(e);
        }
    }

    private void writeCharacteristic(String data) {
        characteristic.setValue(data.getBytes());
        boolean writeStarted = gatt.writeCharacteristic(characteristic);
        if (!writeStarted) {
            clientUiCallback.onMessageFailed();
        }
    }

    public class ServerBinder extends Binder implements ILocalBinder {

        public GattServerService getService() {
            return GattServerService.this;
        }
    }

}

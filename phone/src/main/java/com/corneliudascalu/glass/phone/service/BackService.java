package com.corneliudascalu.glass.phone.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;
import com.corneliudascalu.glass.phone.domain.message.EventMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectingToServerStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectionErrorMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.NoNetworkStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.RegisteredGcmStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.DeviceUnsupportedStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.RecoverableErrorStatusMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@osf-global.com>
 */
public class BackService extends Service {

    public static final String TAG = "BackService";

    public static final String GCM_REGISTRATION_ID_KEY = "GcmRegistrationId";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;

    public static final String SENDER_ID = "139014820790";

    public static final String PREF_NAME = BackService.class.getSimpleName();

    private GoogleCloudMessaging gcm;

    private String gcmRegistrationId;


    private DeviceRepository deviceRepository;

    private Device device;

    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        deviceRepository = new DeviceRepository();

        device = createDevice();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isPlayServicesConnected()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            gcmRegistrationId = getGcmRegistrationId();
            if (TextUtils.isEmpty(gcmRegistrationId)) {
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    registerGcmInBackground();
                } else {
                    sendEventMessage(new NoNetworkStatusMessage("No active network detected"));
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isPlayServicesConnected() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (result != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                sendEventMessage(new RecoverableErrorStatusMessage(result, "Recoverable GCM error"));
                // GooglePlayServicesUtil.getErrorDialog(result, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                sendEventMessage(new DeviceUnsupportedStatusMessage(result, "Device not supported"));
            }
            return false;
        }
        return true;
    }

    private String getGcmRegistrationId() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String gcmRegistrationId = prefs.getString(GCM_REGISTRATION_ID_KEY, "");
        if (TextUtils.isEmpty(gcmRegistrationId)) {
            return "";
        }
        return gcmRegistrationId;
    }

    private void registerGcmInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcmRegistrationId = gcm.register(SENDER_ID);
                    device.setToken(gcmRegistrationId);
                    sendEventMessage(new RegisteredGcmStatusMessage("Device registered to GCM",
                            gcmRegistrationId));
                    msg = registerToServer();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    sendEventMessage(new ConnectionErrorMessage(ex.getMessage(), ex));
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private String registerToServer() throws IOException {
        boolean serverRegistered = sendRegistrationIdToBackend();
        String msg = "";
        if (serverRegistered) {
            sendEventMessage(new ConnectingToServerStatusMessage(
                    ConnectingToServerStatusMessage.Status.Success,
                    "Successfully sent registration id to server"));
            storeRegistrationId(gcmRegistrationId);
            msg = "Successfully registered to server";
        } else {
            msg = "Failed to register to server";
            sendEventMessage(new ConnectingToServerStatusMessage(
                    ConnectingToServerStatusMessage.Status.Failed,
                    "Failed to send registration id to server"));
        }
        return msg;
    }

    private boolean sendRegistrationIdToBackend() throws IOException {
        return deviceRepository.registerToServer(device);
    }

    private void storeRegistrationId(String gcmRegistrationId) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putString(GCM_REGISTRATION_ID_KEY, gcmRegistrationId).apply();
    }

    public void registerToServerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return registerToServer();
                } catch (IOException e) {
                    return "Failed to register to server";
                }
            }
        }.execute();
    }

    private Device createDevice() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                return new Device(possibleEmail, "");
            }
        }
        return null;
    }

    private void sendEventMessage(EventMessage message) {
        EventBus.getDefault().post(message);
    }
}

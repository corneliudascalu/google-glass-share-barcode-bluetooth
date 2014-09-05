package com.corneliudascalu.glass.phone.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.corneliudascalu.glass.device.data.DeviceRepository;
import com.corneliudascalu.glass.device.model.Device;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

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

    @Override
    public void onCreate() {
        super.onCreate();
        deviceRepository = new DeviceRepository();
        String deviceId = Settings.Secure
                .getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                device = new Device(possibleEmail, deviceId);
                break;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isPlayServicesConnected()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            gcmRegistrationId = getGcmRegistrationId();
            if (TextUtils.isEmpty(gcmRegistrationId)) {
                registerInBackground();
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
                // TODO Send intent for activity
                // GooglePlayServicesUtil.getErrorDialog(result, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
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

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcmRegistrationId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + gcmRegistrationId;

                    boolean registered = sendRegistrationIdToBackend();
                    if (registered) {
                        storeRegistrationId(gcmRegistrationId);
                    } else {
                        msg = "Failed to register to server";
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            private void storeRegistrationId(String gcmRegistrationId) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREF_NAME,
                        Context.MODE_PRIVATE);
                prefs.edit().putString(GCM_REGISTRATION_ID_KEY, gcmRegistrationId).apply();
            }

            private boolean sendRegistrationIdToBackend() throws IOException {
                device.setToken(gcmRegistrationId);
                return deviceRepository.registerToServer(device);
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}

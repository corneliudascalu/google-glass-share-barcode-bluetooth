package com.corneliudascalu.glass.device.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.corneliudascalu.glass.device.model.Device;
import com.corneliudascalu.glass.device.model.DeviceList;
import com.corneliudascalu.glass.device.model.DeviceMessage;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceRepositoryImpl implements DeviceRepository {

    private OkHttpClient client = new OkHttpClient();

    @Override
    public List<Device> getDevices() throws IOException {
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/devices.php";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();

        /**
         * {"Devices":
         * [{"token":"9480ace500012eb931f847fde2c5508c6c9769871d7c9324132dba67ba4dc9d0","name":"Sergiu Grigoriev's iPhone","ostype":1},
         * {"token":"APA91bEsmnxK1qnv4jUfoGs3HssMTgM4lMuNJcTOY4p9bjCRMUlot5ODI_lIiGbO5N6K5TYZtpl02kTrf6OQYFAIr8oL1YNP8a0gTKILbmAeYY6KELnP5lDHdYzGJpibfYoi_uYULoa4P79pcrhmJQGioDXtUTt54C0yjOMbAG9wjSQbYWYpFDo","name":"corneliu.dascalu@gmail.com","ostype":2},
         * {"token":"asdasdasd","name":"Vasiles ipadddddd","ostype":1},
         * {"token":"dfaerq34rfvrae4tq3asdfasdf","name":"Cornelius Nexus 5","ostype":2}]}
         */
        Gson gson = new GsonBuilder().create();
        /*List<Device> devices = gson.fromJson(json, new TypeToken<List<Device>>() {
        }.getType());*/
        DeviceList devices = gson.fromJson(json, DeviceList.class);
        return devices.getDevices();
    }

    @Override
    public boolean sendData(Device device, String data) throws IOException {
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/push.php";

        DeviceMessage deviceMessage = DeviceMessage.create(device, data);
        String body = new GsonBuilder().create().toJson(deviceMessage);

        RequestBody requestBody = RequestBody.create(MediaType.parse(body), body);

        Request request = new Request.Builder().url(url).put(requestBody).build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    @Override
    public boolean registerToServer(Device device) throws IOException {

        DeviceMessage deviceMessage = DeviceMessage.create(device, null);
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/register.php";
        Gson gson = new GsonBuilder().create();
        String deviceData = gson.toJson(deviceMessage);
        RequestBody requestBody = RequestBody.create(MediaType.parse(deviceData), deviceData);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    @Override
    public void saveSelectedDevice(Context context, Device device) {
        Gson gson = new GsonBuilder().create();
        String deviceJson = gson.toJson(device);
        SharedPreferences prefs = context.getSharedPreferences(Device.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putString(Device.SELECTED_DEVICE_KEY, deviceJson).apply();
    }

    @Override
    public Device getSelectedDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Device.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        String deviceString = prefs.getString(Device.SELECTED_DEVICE_KEY, "");
        if (!TextUtils.isEmpty(deviceString)) {
            return new GsonBuilder().create().fromJson(deviceString, Device.class);
        } else {
            return Device.NO_DEVICE;
        }
    }

}

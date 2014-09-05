package com.corneliudascalu.glass.device.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.corneliudascalu.glass.device.model.Device;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceRepository {

    private OkHttpClient client = new OkHttpClient();

    public List<Device> getDevices() throws IOException {
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/devices.php";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();

        // json= "[{\"name\":\"asd\",\"token\":\"asdasd\"},{\"name\":\"asd\",\"token\":\"asdasd\"},{\"name\":\"asd\",\"token\":\"asdasd\"}]";
        Gson gson = new GsonBuilder().create();
        List<Device> devices = gson.fromJson(json, new TypeToken<List<Device>>() {
        }.getType());
        return devices;
    }

    public boolean sendData(Device device, String data) throws IOException {
        String url = "http://google.com" + "?data=" + data + "&device=" + device.getToken();
        Request request = new Request.Builder().url(url).post(null).build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    public boolean registerToServer(Device device) throws IOException {
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/register.php";
        Gson gson = new GsonBuilder().create();
        String deviceData = gson.toJson(device);
        RequestBody requestBody = RequestBody.create(MediaType.parse(deviceData), deviceData);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

}

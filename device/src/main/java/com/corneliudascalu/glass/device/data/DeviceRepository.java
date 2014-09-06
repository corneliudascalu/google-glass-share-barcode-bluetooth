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

    public boolean sendData(Device device, String data) throws IOException {
        String url = "http://constantcontact.ofactory.biz/PushNotifService/gglass/push.php";

        DeviceMessage deviceMessage = DeviceMessage.create(device, data);
        String body = new GsonBuilder().create().toJson(deviceMessage);

        RequestBody requestBody = RequestBody.create(MediaType.parse(body), body);

        Request request = new Request.Builder().url(url).put(requestBody).build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

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

}

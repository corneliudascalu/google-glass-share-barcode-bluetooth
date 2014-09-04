package com.corneliudascalu.glass.app2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.corneliudascalu.glass.app2.model.Device;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceRepository {

    private OkHttpClient client = new OkHttpClient();

    public List<Device> getDevices() throws IOException {
        String url = "http://google.com";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();

        json
                = "[{\"name\":\"asd\",\"token\":\"asdasd\"},{\"name\":\"asd\",\"token\":\"asdasd\"},{\"name\":\"asd\",\"token\":\"asdasd\"}]";
        Gson gson = new GsonBuilder().create();
        List<Device> devices = gson.fromJson(json, new TypeToken<List<Device>>() {
        }.getType());
        return devices;
    }

}

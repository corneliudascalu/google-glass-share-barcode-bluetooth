package com.corneliudascalu.glass.app2;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

import com.corneliudascalu.glass.app2.model.Device;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceCardAdapter extends CardScrollAdapter {

    private ArrayList<Device> devices = new ArrayList<Device>();

    private ArrayList<Card> cards = new ArrayList<Card>();

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int i) {
        return cards.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return cards.get(i).getView(view, viewGroup);
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    public void setDevices(Context context, ArrayList<Device> devices) {
        this.devices = devices;
        cards.clear();
        for (Device device : devices) {
            Card card = new Card(context);
            card.setText(device.getName());
            card.setFootnote(device.getToken());
            cards.add(card);
        }
        notifyDataSetChanged();
    }
}

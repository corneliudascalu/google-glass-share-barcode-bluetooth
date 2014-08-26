package com.corneliudascalu.testglass;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class DeviceCardScrollAdapter extends CardScrollAdapter {

    private ArrayList<BluetoothDevice> devices;

    private ArrayList<Card> cards;

    public DeviceCardScrollAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super();
        this.devices = devices;
        cards = new ArrayList<Card>(devices.size());
        for (BluetoothDevice device : devices) {
            Card card = new Card(context);
            card.setText(device.getName());
            card.setFootnote(device.getAddress());
            cards.add(card);
        }
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        return cards.get(position).getView();
    }

    @Override
    public int getPosition(Object o) {
        return devices.indexOf(o);
    }
}

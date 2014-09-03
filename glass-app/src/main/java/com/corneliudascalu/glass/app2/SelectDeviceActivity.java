package com.corneliudascalu.glass.app2;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SelectDeviceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
    }

    private View buildView() {
        Card card = new Card(this);

        card.setText("Select device");
        return card.getView();

    }
}

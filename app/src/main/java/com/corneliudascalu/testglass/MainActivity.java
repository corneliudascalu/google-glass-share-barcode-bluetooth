package com.corneliudascalu.testglass;

import com.google.android.glass.app.Card;

import com.github.barcodeeye.scan.CaptureActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends BluetoothConnectedActivity {

    public static final String TAG = "Main";

    private View cardView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(buildView());

    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link Card} class.
     */
    private View buildView() {
        Card card = new Card(this);

        card.setText(R.string.preparing_message);
        cardView = card.getView();

        return cardView;
    }

    @Override
    public void onDeviceConnectionEstablished() {
        startActivity(CaptureActivity.newIntent(this));
    }

}

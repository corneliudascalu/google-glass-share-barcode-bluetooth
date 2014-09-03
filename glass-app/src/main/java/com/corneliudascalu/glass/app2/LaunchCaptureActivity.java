package com.corneliudascalu.glass.app2;

import com.google.android.glass.app.Card;

import com.github.barcodeeye.scan.CaptureActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class LaunchCaptureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
        startActivity(CaptureActivity.newIntent(this));
    }

    private View buildView() {
        Card card = new Card(this);

        card.setText("Launching capture...");
        return card.getView();

    }
}

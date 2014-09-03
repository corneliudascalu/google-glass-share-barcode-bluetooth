package com.corneliudascalu.glass.app2;

import com.github.barcodeeye.scan.CaptureActivity;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class LaunchCaptureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(CaptureActivity.newIntent(this));
    }
}

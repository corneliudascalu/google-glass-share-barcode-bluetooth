package com.corneliudascalu.glass.app2;

import android.app.Application;
import com.crashlytics.android.Crashlytics;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class GlassApp extends Application {

    private static GlassApp instance;
    public static GlassApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
        instance = this;
    }
}

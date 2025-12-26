package com.example.oya.myplaces;

import android.app.Application;
import org.osmdroid.config.Configuration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(getPackageName());
    }
}
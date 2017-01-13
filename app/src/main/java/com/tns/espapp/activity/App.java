package com.tns.espapp.activity;

import android.app.Application;

import com.tns.espapp.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by TNS on 12/22/2016.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
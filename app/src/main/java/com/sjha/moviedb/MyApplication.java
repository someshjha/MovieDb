package com.sjha.moviedb;

import com.activeandroid.ActiveAndroid;

/**
 * Created by sjha on 15-10-26.
 */
public class MyApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}

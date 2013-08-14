package com.marakana.android.yamba;

import android.app.Application;

import com.marakana.android.yamba.svc.YambaService;


public class YambaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        YambaService.startPoller(this);
    }
}

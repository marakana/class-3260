package com.marakana.android.yamba;

import android.app.Application;

import com.marakana.android.yamba.svc.YambaService;


public class YambaApplication extends Application {

    // the application object is created whenever the
    // application starts.  putting the call to startPoller
    // here causes the application to start polling,
    // as soon as it is started.
    @Override
    public void onCreate() {
        super.onCreate();
        YambaService.startPoller(this);
    }
}

package com.marakana.android.yamba.svc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;


public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BOOT";

    @Override
    public void onReceive(Context ctxt, Intent intent) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Boot"); }
        YambaService.startPoller(ctxt);
    }
}

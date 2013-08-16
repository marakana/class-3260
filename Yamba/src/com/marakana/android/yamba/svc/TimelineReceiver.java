package com.marakana.android.yamba.svc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.TimelineActivity;
import com.marakana.android.yamba.YambaContract;


public class TimelineReceiver extends BroadcastReceiver {
    private static final String TAG = "UPDATE";

    private static final int NOTIFICATION_ID = 7;
    private static final int NOTIFICATION_INTENT_ID = 13;


    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "New status"); }

        String title = "New Status!";
        String message = " new messages";

        int count = intent.getIntExtra(YambaContract.TIMELINE_UPDATE_COUNT, -1);
        if (0 >= count) { return; }

        PendingIntent pi = PendingIntent.getActivity(
                ctxt,
                NOTIFICATION_INTENT_ID,
                new Intent(ctxt, TimelineActivity.class),
                0);

        ((NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE))
        .notify(
                NOTIFICATION_ID,
                new Notification.Builder(ctxt)
                .setContentTitle(title)
                .setContentText(count + message)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .getNotification());
        //.build()   // works as of version 16
    }
}

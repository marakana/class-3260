package com.marakana.android.yamba.svc;

import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.R;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;


public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final int POLL_REQUEST = 42;

    private static final String PARAM_STATUS = "YambaService.STATUS";
    private static final String PARAM_OP = "YambaService.OP";

    private static final int OP_POST_COMPLETE = -1;
    private static final int OP_POST = -2;
    private static final int OP_POLL = -3;

    private static class Hdlr extends Handler {
        private final YambaService svc;

        public Hdlr(YambaService svc) { this.svc = svc; }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case OP_POST_COMPLETE:
                    Toast.makeText(svc, msg.arg1, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }

    public static void startPoller(Context ctxt) {
        long t = 1000 * 60 * ctxt.getResources().getInteger(R.integer.poll_interval);
        ((AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE))
            .setInexactRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 100,
                t,
                getPollingIntent(ctxt));
        if (BuildConfig.DEBUG) { Log.d(TAG, "Polling started"); }
    }

    private static PendingIntent getPollingIntent(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL);

        return PendingIntent.getService(
                ctxt,
                POLL_REQUEST,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private volatile Hdlr hdlr;
    private volatile int maxPolls;
    private volatile YambaClient client;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();
        hdlr = new Hdlr(this);
        maxPolls = getResources().getInteger(R.integer.poll_max);
        client = new YambaClient(
                "student",
                "password",
                "http://yamba.marakana.com/api");
    }

    // runs on a daemon thread
    @Override
    protected void onHandleIntent(Intent i) {
        int op = i.getIntExtra(PARAM_OP, 0);
        if (BuildConfig.DEBUG) { Log.d(TAG, "Processing: " + op); }
        switch (op) {
            case OP_POST:
                doPost(i.getStringExtra(PARAM_STATUS));
                break;

            case OP_POLL:
                doPoll();
                break;

            default:
                 Log.w(TAG, "Unrecognized op ignored: " + op);
        }
    }

    private void doPost(String status) {
        int ret = R.string.post_failed;
        try {
            client.postStatus(status);
            ret = R.string.post_succeeded;
        }
        catch (YambaClientException e) {
            Log.e(TAG, "Post failed", e);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Post complete: " + (R.string.post_succeeded == ret));
        }
        Message.obtain(hdlr, OP_POST_COMPLETE, ret, 0).sendToTarget();
    }

    private void doPoll() {
        List<Status> timeline = null;
        try { timeline = client.getTimeline(maxPolls); }
        catch (YambaClientException e) {
            Log.e(TAG, "Post failed", e);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Poll complete: " + ((null == timeline) ? -1 : timeline.size()));
        }
        if (null != timeline) { processTimeline(timeline); }
    }

    private void processTimeline(List<Status> timeline) {
        for (Status status: timeline) {
            Log.i(TAG, "status: " + status.getMessage());
        }
    }
}

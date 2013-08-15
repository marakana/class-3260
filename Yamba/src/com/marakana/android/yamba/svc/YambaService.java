package com.marakana.android.yamba.svc;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.R;
import com.marakana.android.yamba.YambaContract;
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

    // when this handler receives a message,
    // it looks at Message.what to see if it is
    // the message type it handles.  if it is (OP_POST_COMPLETE)
    // it looks at Message.arg1 to see which string
    // resource to post.
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

    // called from StatusActivity.post.  turn the post request
    // into an intent and fire it off.  it will later
    // be received on a daemon thread, in onHandleIntent
    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }

    // get the alarm manager to send a PendingIntent to us,
    // every so often.
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

    // this is the pending intent that the alarm manager will
    // send us.  it will be received on the daemon thread,
    // by onHandleIntent.
    private static PendingIntent getPollingIntent(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL);

        return PendingIntent.getService(
                ctxt,
                POLL_REQUEST,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // these variables are initialized on the UI thread (onCreate)
    // but used on the daemon thread (onHandleIntent).  they must
    // be volatile.
    private volatile Hdlr hdlr;
    private volatile int maxPolls;
    private volatile YambaClient client;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();

        // since this handler is created on the UI thread,
        // it's handleMessage method will run on the UI thread.
        hdlr = new Hdlr(this);

        // maxPolls is configurable: it is a resource
        maxPolls = getResources().getInteger(R.integer.poll_max);

        client = new YambaClient(
                "student",
                "password",
                "http://yamba.marakana.com/api");
    }

    // runs on a daemon thread
    // use the PARAM_OP code in the intent to figure out what to do
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

    // post a message to the Yamba server
    // this is exactly the code that use to be in the AsyncTask
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

        // send a message that will cause a Toast, on the UI thread.
        Message.obtain(hdlr, OP_POST_COMPLETE, ret, 0).sendToTarget();
    }

    // poll the Yamba server for recent posts.
    private void doPoll() {
        List<Status> timeline = null;
        try { timeline = client.getTimeline(maxPolls); }
        catch (YambaClientException e) {
            Log.e(TAG, "Post failed", e);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Poll complete: " + ((null == timeline) ? -1 : timeline.size()));
        }
        processTimeline(timeline);
    }

    // insert new timeline data into the database
    private int processTimeline(List<Status> timeline) {
        if ((null == timeline) || timeline.isEmpty()) { return 0; }

        long latest = getMaxTimestamp();
        List<ContentValues> vals = new ArrayList<ContentValues>();
        for (Status status: timeline) {
            long t = status.getCreatedAt().getTime();
            if (t <= latest) { continue; }

            ContentValues row = new ContentValues();
            row.put(YambaContract.Timeline.Column.ID, Long.valueOf(status.getId()));
            row.put(YambaContract.Timeline.Column.TIMESTAMP, Long.valueOf(t));
            row.put(YambaContract.Timeline.Column.USER, status.getUser());
            row.put(YambaContract.Timeline.Column.STATUS, status.getMessage());

            Log.d(TAG, "Insert: " + row);
            vals.add(row);
        }

        return getContentResolver().bulkInsert(
                YambaContract.Timeline.URI,
                vals.toArray(new ContentValues[vals.size()]));
    }

    // find the timestamp on the newest record in the db
    private long getMaxTimestamp() {
        Cursor c = null;
        long mx = Long.MIN_VALUE;
        try {
            c = getContentResolver().query(
                    YambaContract.Timeline.URI,
                    new String[] { YambaContract.Timeline.Column.MAX_TIMESTAMP },
                    null,
                    null,
                    null);
            if (c.moveToNext()) { mx = c.getLong(0); }
        }
        finally {
            if (null != c) { c.close(); }
        }
        return mx;
    }
}


package com.marakana.android.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class StatusActivity extends Activity {

    private static final long T = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d("###", "created: " + T + ", " + this);
        }

        String submit = getString(R.string.status_submit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}


package com.marakana.android.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;


public class StatusActivity extends Activity {

    private int okColor;
    private int warnColor;
    private int errColor;
    private int maxStatusLen;
    private int warnMax;
    private int errMax;
    private TextView viewCount;
    private EditText viewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources rez = getResources();

        okColor = rez.getColor(R.color.status_ok);
        warnColor = rez.getColor(R.color.status_warn);
        errColor = rez.getColor(R.color.status_err);

        maxStatusLen = rez.getInteger(R.integer.status_max_len);
        warnMax = rez.getInteger(R.integer.warn_max);
        errMax = rez.getInteger(R.integer.err_max);

        setContentView(R.layout.activity_status);

        viewCount = (TextView) findViewById(R.id.status_count);
        viewStatus = (EditText) findViewById(R.id.status_status);
        viewStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) { updateCount(); }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {  }
        });
    }

     void updateCount() {
         int n = maxStatusLen - viewStatus.getText().length();

         int c;
         if (n > warnMax) { c = okColor; }
         else if (n > errMax) { c = warnColor; }
         else { c = errColor; }

         viewCount.setText(String.valueOf(n));
         viewCount.setTextColor(c);
    }
}

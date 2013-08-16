/* $Id: $
   Copyright 2013, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.marakana.android.yamba.svc.YambaService;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class StatusFragment extends Fragment  {
    private static final String TAG = "STATUS";

// Async task implementation of Post.
// This is better done with a Service
//
//     class Poster extends AsyncTask<String, Void, Integer> {
//        private final Context ctxt;
//
//        public Poster(Context ctxt) { this.ctxt = ctxt; }
//
//        // Runs on daemon thread
//        @Override
//        protected Integer doInBackground(String... status) {
//            YambaClient client = new YambaClient(
//                    "student",
//                    "password",
//                    "http://yamba.marakana.com/api");
//
//            int ret = R.string.post_failed;
//            try {
//                client.postStatus(status[0]);
//                ret = R.string.post_succeeded;
//            }
//            catch (YambaClientException e) {
//                Log.e(TAG, "Post failed", e);
//            }
//
//            return Integer.valueOf(ret);
//        }
//
//        // Runs on UI thread
//        @Override
//        protected void onPostExecute(Integer ret) {
//            Toast.makeText(ctxt, ret.intValue(), Toast.LENGTH_LONG).show();
//           poster = null;
//        }
//    }
//
//    static Poster poster;


    private int maxStatusLen;
    private int warnMax;
    private int errMax;
    private int okColor;
    private int warnColor;
    private int errColor;
    private TextView viewCount;
    private EditText viewStatus;
    private Button buttonSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {
        Resources rez = getResources();

        // use resources for configurable parameters
        // integer resources are not well documented but do exist
        maxStatusLen = rez.getInteger(R.integer.status_max_len);
        warnMax = rez.getInteger(R.integer.warn_max);
        errMax = rez.getInteger(R.integer.err_max);

        // get the colors from resources
        okColor = rez.getColor(R.color.status_ok);
        warnColor = rez.getColor(R.color.status_warn);
        errColor = rez.getColor(R.color.status_err);

        View v = inflater.inflate(R.layout.fragment_status, root, false);

        // wire up the button so that the post()
        // method is called, when it is pushed
        buttonSubmit = (Button) v.findViewById(R.id.status_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) { post(); }
        });

        // cache a pointer to the view containing the character count
        viewCount = (TextView) v.findViewById(R.id.status_count);

        // each keystroke in the EditText box will call updateCount()
        viewStatus = (EditText) v.findViewById(R.id.status_status);
        viewStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) { updateCount(); }

            @Override
            public void beforeTextChanged(CharSequence str, int s, int c, int a) { }

            @Override
            public void onTextChanged(CharSequence str, int s, int c, int a) {  }
        });

        return v;
    }

    // on each keystroke:
    //    - enable the submit button if there is a legal message
    //    - set the count to be the number of chars remaining
    //      in a legal message
    //    - set the color of the count text to warn if message
    //      is nearing the max length.
    void updateCount() {
        int n = viewStatus.getText().length();

        buttonSubmit.setEnabled(checkStatusLen(n));

        n = maxStatusLen - n;

        int c;
        if (n > warnMax) { c = okColor; }
        else if (n > errMax) { c = warnColor; }
        else { c = errColor; }

        viewCount.setText(String.valueOf(n));
        viewCount.setTextColor(c);
    }

    // post a message to the Yamba server.
    // note that the actual post MUST NOT happen on
    // the UI thread.  An AsyncTask is one way to get it off.
    // another, preferred, way, uses an IntentService.
    void post() {
//         if (null != poster) { return; }

        String status = viewStatus.getText().toString();
        if (BuildConfig.DEBUG) { Log.d(TAG, "Posting: " + status); }

        if (!checkStatusLen(status.length())) { return; }

        viewStatus.setText("");

        // let the service handle the post
        YambaService.post(getActivity(), status);
//         poster = new Poster(getApplicationContext());
//         poster.execute(status.toString());
    }

    private boolean checkStatusLen(int n) {
        return (0 < n) && (maxStatusLen >= n);
    }
}

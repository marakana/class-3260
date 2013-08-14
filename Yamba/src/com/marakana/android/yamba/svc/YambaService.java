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
package com.marakana.android.yamba.svc;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final String PARAM_STATUS = "YambaService.STATUS";

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }


    public YambaService() { super(TAG); }

    @Override
    protected void onHandleIntent(Intent i) {
        YambaClient client = new YambaClient(
                "student",
                "password",
                "http://yamba.marakana.com/api");

        try { client.postStatus(i.getStringExtra(PARAM_STATUS)); }
        catch (YambaClientException e) {
            Log.e(TAG, "Post failed", e);
        }


    }
}

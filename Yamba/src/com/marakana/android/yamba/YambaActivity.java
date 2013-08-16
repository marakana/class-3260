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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

// TO USE:
// 1) create an instance of this object in Activity.onCreate
//menuMgr = new YambaStatusBarManager(this);
//menuMgr.init();
//
// 2) add these two methods to the Activity
//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//    return menuMgr.onCreateOptionsMenu(menu);
//}
//
//@Override
//public boolean onOptionsItemSelected(MenuItem item) {
//    return  (menuMgr.onOptionsItemSelected(item))
//            ? true
//            : super.onOptionsItemSelected(item);
//}

public abstract class YambaActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yamba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_status:
                nextPage(StatusActivity.class);
                break;

            case R.id.menu_timeline:
                nextPage(TimelineActivity.class);
                break;

            case android.R.id.home:
                Toast.makeText(this, R.string.about, Toast.LENGTH_LONG).show();
                break;

            default:
                return false;
        }

        return true;
    }

    private void nextPage(Class<?> klass) {
        Intent i = new Intent(this, klass);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }
}

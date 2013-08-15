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
package com.marakana.android.yamba.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class YambaDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "yamba.db";
    public static final int VERSION = 1;

    static final String TABLE = "timeline";
    static final String COL_ID = "id";
    static final String COL_TIMESTAMP = "createdAt";
    static final String COL_USER = "user";
    static final String COL_STATUS = "status";


    public YambaDBHelper(Context ctxt) {
        super(ctxt, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE + "("
                    + COL_ID + " integer primary key,"
                    + COL_TIMESTAMP + " integer,"
                    + COL_USER + " text,"
                    + COL_STATUS + " text"
                    + ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int prev, int curr) {
        db.execSQL("drop table " + TABLE);
        onCreate(db);

    }

}

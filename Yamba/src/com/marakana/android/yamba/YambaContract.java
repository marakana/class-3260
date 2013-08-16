package com.marakana.android.yamba;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class YambaContract {
    private YambaContract() { }

    public static final long VERSION = 1;

    public static final String AUTHORITY
         = "com.marakana.android.yamba";

    public static final Uri BASE_URI = new Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .build();

    // timeline update notifications
    public static final String BROADCAST_TIMELINE_UPDATE
        = "com.marakana.android.yamba.action.NEW_STATUS";
    public static final String TIMELINE_UPDATE_COUNT
        = "com.marakana.android.yamba.action.NEW_STATUS_COUNT";

    public static final class Timeline {
        private Timeline() { }

        public static final String TABLE = "timeline";

        private static final String MINOR_TYPE = "/vnd." + AUTHORITY;

        public static final String ITEM_TYPE
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + MINOR_TYPE;
        public static final String DIR_TYPE
            = ContentResolver.CURSOR_DIR_BASE_TYPE + MINOR_TYPE;

        public static final Uri URI
            = BASE_URI.buildUpon().appendPath(TABLE).build();

        public static final class Column {
            private Column() { }

            public static final String ID = BaseColumns._ID;
            public static final String TIMESTAMP = "time";
            public static final String MAX_TIMESTAMP = "maxTime";
            public static final String USER = "user";
            public static final String STATUS = "message";
        }
    }
}
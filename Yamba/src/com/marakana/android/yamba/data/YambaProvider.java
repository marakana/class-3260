package com.marakana.android.yamba.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;

import com.marakana.android.yamba.YambaContract;


public class YambaProvider extends ContentProvider {
    private YambaDBHelper db;


    //  scheme         authority               path
    //                                         table/#
    // content://com.marakana.android.yamba/timeline/7
    private static final int TIMELINE_ITEM_TYPE = 1;
    private static final int TIMELINE_DIR_TYPE = 2;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM_TYPE);
        MATCHER.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE,
                TIMELINE_DIR_TYPE);
    }

    private static final ColumnMap TIMELINE_COL_MAP = new ColumnMap.Builder()
        .addColumn(
                YambaContract.Timeline.Column.ID,
                YambaDBHelper.COL_ID,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Timeline.Column.TIMESTAMP,
                YambaDBHelper.COL_TIMESTAMP,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Timeline.Column.USER,
                YambaDBHelper.COL_USER,
                ColumnMap.Type.STRING)
        .addColumn(
                YambaContract.Timeline.Column.STATUS,
                YambaDBHelper.COL_STATUS,
                ColumnMap.Type.STRING)
        .build();

    private static final ProjectionMap TIMELINE_PROJ_MAP = new ProjectionMap.Builder()
        .addColumn(YambaContract.Timeline.Column.ID, YambaDBHelper.COL_ID)
        .addColumn(YambaContract.Timeline.Column.TIMESTAMP, YambaDBHelper.COL_TIMESTAMP)
        .addColumn(YambaContract.Timeline.Column.USER, YambaDBHelper.COL_USER)
        .addColumn(YambaContract.Timeline.Column.STATUS, YambaDBHelper.COL_STATUS)
        .addColumn(
                YambaContract.Timeline.Column.MAX_TIMESTAMP,
                "max(" + YambaDBHelper.COL_TIMESTAMP + ")")
        .build();

    @Override
    public boolean onCreate() {
        db = new YambaDBHelper(getContext());
        return null != db;
    }

    @Override
    public String getType(Uri uri) {

        switch (MATCHER.match(uri)) {
            case TIMELINE_DIR_TYPE:
                return YambaContract.Timeline.DIR_TYPE;
            case TIMELINE_ITEM_TYPE:
                return YambaContract.Timeline.ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unrecognized uri: " + uri);
        }
    }


    // "id = 7; drop table timeline;"
    // "id = ?1",  "7"


    // select <columns>   -- projection
    //     from <table>   -- table
    //     where <selection>  <selargs>  -- restriction
    //     order by <sort cols>  --- sort

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        long pk = -1;
        switch (MATCHER.match(uri)) {
            case TIMELINE_ITEM_TYPE:
                pk = ContentUris.parseId(uri);
                break;
            case TIMELINE_DIR_TYPE:
                break;

            default:
                throw new IllegalArgumentException("Unrecognized uri: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(YambaDBHelper.TABLE);

        qb.setProjectionMap(TIMELINE_PROJ_MAP.getProjectionMap());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            qb.setStrict(true);
        }

        if (0 < pk) { qb.appendWhere(YambaDBHelper.COL_ID + "=" + pk); }

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] rows) {
        switch (MATCHER.match(uri)) {
            case TIMELINE_DIR_TYPE:
                break;
            default:
                throw new IllegalArgumentException("Unrecognized uri: " + uri);
        }

        SQLiteDatabase sdb = getDb();
        int count = 0;
        try {
            sdb.beginTransaction();

            for (ContentValues row: rows) {
                row = TIMELINE_COL_MAP.translateCols(row);
                if (0 < sdb.insert(YambaDBHelper.TABLE, null, row)) {
                    count++;
                }
            }

            sdb.setTransactionSuccessful();
        }
        finally {
            sdb.endTransaction();
        }

        if (0 < count) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues vals) {
        throw new UnsupportedOperationException("delete not supported");
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        throw new UnsupportedOperationException("delete not supported");
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        throw new UnsupportedOperationException("update not supported");
    }

    private SQLiteDatabase getDb() { return db.getWritableDatabase(); }
}

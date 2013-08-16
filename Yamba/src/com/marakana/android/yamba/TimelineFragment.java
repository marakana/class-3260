package com.marakana.android.yamba;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TimelineFragment extends ListFragment implements LoaderCallbacks<Cursor>{
    private static final String TAG = "TIMELINE";

    private static final int TIMELINE_LOADER = 666;

    private static final String[] PROJ = new String[] {
        YambaContract.Timeline.Column.ID,
        YambaContract.Timeline.Column.USER,
        YambaContract.Timeline.Column.TIMESTAMP,
        YambaContract.Timeline.Column.STATUS,
    };

    private static final String[] FROM = new String[PROJ.length - 1];
    static { System.arraycopy(PROJ, 1, FROM, 0, FROM.length); }

    private static final int[] TO = new int[] {
        R.id.timeline_user,
        R.id.timeline_time,
        R.id.timeline_status
    };

    static class TimelineBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor c, int idx) {
            if (R.id.timeline_time != view.getId()) { return false; }

            CharSequence s = "long ago";
            long t = c.getLong(idx);
            if (0 < t) { s = DateUtils.getRelativeTimeSpanString(t); }
            ((TextView) view).setText(s);
            return true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Create loader"); }
        return new CursorLoader(
                getActivity(),
                YambaContract.Timeline.URI,
                PROJ,
                null,
                null,
                YambaContract.Timeline.Column.TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "New cursor: " + cur); }
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(cur);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Reset cursor"); }
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {

        View v = inflater.inflate(R.layout.fragment_timeline, root, false);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(), // context
                R.layout.timeline_row,
                null,
                FROM,
                TO,
                0);

        adapter.setViewBinder(new TimelineBinder());
        setListAdapter(adapter);

        getLoaderManager().initLoader(TIMELINE_LOADER, null, this);

        return v;
    }
}

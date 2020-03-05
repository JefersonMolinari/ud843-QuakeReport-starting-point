package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    private String url;

    public EarthquakeLoader(Context context, String url){
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading called...");
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground called...");
        if(TextUtils.isEmpty(url)){
            Log.w(LOG_TAG, "LoadInBackgroung failed, url null or empty");
            return null;
        }

        return QueryUtils.fetchEarthquakeData(url);
    }
}

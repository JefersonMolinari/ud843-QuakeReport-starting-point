/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthAdapter adapter;
    private TextView emptyStateTextView;
    private View spinner;

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    public static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-12-01&minmagnitude=7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView listView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.emptyState);
        listView.setEmptyView(emptyStateTextView);

        spinner = findViewById(R.id.loading_spinner);

        adapter = new EarthAdapter(this, new ArrayList<Earthquake>());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "initLoader called...");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            spinner.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "OnCreateLoader called...");
        Loader<List<Earthquake>> loader = new EarthquakeLoader(this, USGS_REQUEST_URL);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        spinner.setVisibility(View.GONE);

        Log.i(LOG_TAG, "onLoadFinished called...");

        emptyStateTextView.setText(R.string.no_earthquakes_found);
        adapter.clear();

        if (earthquakes != null && !earthquakes.isEmpty()){
            adapter.addAll(earthquakes);
        } else{
            Log.w(LOG_TAG, "Load Finished, no data added to adapter");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG, "onLoadReset, adapter Cleared");
        adapter.clear();
    }
}

// TODO Quake Report app has log messages added throughout the code, test out the following scenarios:
//  Rotate the device
//  Go to the home screen and return to app
//  Press back button
//  Open Recent tasks
//  Switch to different app
//  Return to app
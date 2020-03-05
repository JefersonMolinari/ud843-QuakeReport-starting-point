package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int RESPONSE_CODE_OK = 200;

    public QueryUtils() {
    }

    public static List<Earthquake> fetchEarthquakeData(String requestURL){
        Log.i(LOG_TAG, "fetchEarthquakeData called...");

//        Uncomment the lines bellow to delay response from API
//        try{
//            Thread.sleep(2000);
//        } catch (InterruptedException e){
//            Log.e(LOG_TAG, "Timer broke", e);
//        }

        URL url = createURL(requestURL);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<Earthquake> earthquakes = extractEarthquakes(jsonResponse);

        return earthquakes;
    }

    private static URL createURL(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == RESPONSE_CODE_OK){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + responseCode);
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Earthquake> extractEarthquakes(String earthQuakeJSON){
        if(TextUtils.isEmpty(earthQuakeJSON)){
            return null;
        }
        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject eqList = new JSONObject(earthQuakeJSON);
            JSONArray earthquakesArray = eqList.getJSONArray("features");

            for (int i = 0; i < earthquakesArray.length(); i++){
                JSONObject earthquake = earthquakesArray.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject("properties");

                String location = properties.getString("place");
                long date = properties.getLong("time");
                double magnitude = properties.getDouble("mag");
                String url = properties.getString("url");

                earthquakes.add(new Earthquake(location, date, magnitude, url));
            }

        } catch (JSONException e){
            Log.e("QueryUtils","Problem parsing the earthquake JSON results", e );
        }

        return earthquakes;
    }
}

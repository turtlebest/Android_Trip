package com.nyu.cs9033.eta.controllers;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Trip service for updating location in backend.
 *
 * @author      Jessica Huang
 * @version     1.0
 */
public class TripService extends IntentService {
    private static final String TAG = "TripService";
    private static final int POLL_INTERVAL = 1000 * 15; // 15 seconds
    public String longitude;
    public String latitude;

    public TripService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url_string = "http://cs9033-homework.appspot.com/";
        try {
            getLocation();
            updateTrip(url_string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start or stop the service to update location.
     *
     * @param context       the context.
     * @param isOn          update location need to active or not.
     * @throws IOException
     */
    public static void updateLocation(Context context, boolean isOn)
            throws IOException {
        Intent i = new Intent(context, TripService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        if (isOn) {
            Log.i(TAG, "Start");
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            Log.i(TAG, "Success");
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Location service")
                    .setMessage("You have updated your location!")
                    .create();

            alertDialog.getWindow().setType(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    /**
     * Get current location for the device.
     */
    public void getLocation() {
        Log.i(TAG, "Run");
        // Get location from GPS service.
        LocationManager locationManager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        // Add listener.
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider,
                                        int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Get updated location from device.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000, 0, locationListener);
        locationManager.removeUpdates(locationListener);
        String locationProvider = locationManager.GPS_PROVIDER;
        Location lastLocation = locationManager.getLastKnownLocation(locationProvider);
        // Set the data.
        makeUseOfNewLocation(lastLocation);
    }

    /**
     * Set the data for longitude and latitude for current location.
     * @param location current location for device.
     */
    public void makeUseOfNewLocation(Location location) {
        longitude = String.valueOf(location.getLongitude());
        Log.i(TAG, "longitude " + longitude);
        latitude = String.valueOf(location.getLatitude());
        Log.i(TAG, "latitude " + latitude);
    }

    /**
     * Transfer the data to json format to call API.
     *
     * @return json format to call the API.
     */
    public JSONObject toJSON() {
        String unixTime = String.valueOf(System.currentTimeMillis() / 1000L);
        JSONObject json = new JSONObject();
        try {
            json.put("command", "UPDATE_LOCATION");
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("datetime", unixTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * update trip information by calling API service.
     *
     * @param url  The API service website.
     * @return     result from API.
     * @throws     IOException
     */
    public String updateTrip(String url) throws IOException {
        JSONObject jsonObjSend;
        String resultString = "no trip";
        try {
            Log.i(TAG, "Update");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(url);
            jsonObjSend = toJSON();
            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());
            // Set HTTP parameters
            httpPostRequest.setEntity(se);
            HttpResponse response = httpclient.execute(httpPostRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // Read the content stream
                InputStream inStream = entity.getContent();
                // convert content stream to a String
                resultString = convertStreamToString(inStream);
                inStream.close();
                JSONObject jsonObjRes = new JSONObject(resultString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * Convert content stream to a String.
     * @param is  Input stream.
     * @return    Converted string.
     */
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

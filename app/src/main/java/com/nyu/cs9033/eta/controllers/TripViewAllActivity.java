package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nyu.cs9033.eta.R;

/**
 * View all trips activity
 *
 * @author      Jessica Huang
 * @version     1.1
 */
public class TripViewAllActivity extends Activity {
    //private static final String TAG = "TripViewAllActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view_all);

    }

    /**
     * This method should start the activity responsible for
     * viewing a trips.
     *
     * @param view  The view of activity.
     */
    public void startUnStartTripActivity(View view) {
        Intent intent = new Intent(this, TripUnStartActivity.class);
        view.getContext().startActivity(intent);
    }

    /**
     * This method should start the activity responsible for
     * viewing trips.
     *
     * @param view  The view of activity.
     */
    public void startTripHistoryActivity(View view) {
        Intent intent = new Intent(this, TripHistoryActivity.class);
        view.getContext().startActivity(intent);
    }
}

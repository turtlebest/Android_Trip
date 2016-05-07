package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * View started trips activity
 *
 * @author      Jessica Huang
 * @version     1.0
 */
public class TripUnStartActivity extends Activity {
    private static final String TAG = "TripUnStartActivity";
    // The View items
    public ListView item_list;
    Button backBtn;
    ArrayList<HashMap<String, Object>> TripListContent
            = new ArrayList<HashMap<String, Object>>();
    // DB
    public List<Trip> trip;
    public LocalDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        // Trip list.
        item_list = (ListView) findViewById(R.id.listView);

        backBtn = (Button) findViewById(R.id.button6);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Get un started trip from database.
        trip = new ArrayList<Trip>();
        db = new LocalDB(getApplicationContext());
        trip=db.getUnStartTrips();

        if (trip.size() != 0) {
            // Update the list in the trip history view
            // for all trips.
            updateTripList(trip);
        } else {
            // If there is no trip, pop up the alert dialog
            // and return.
            AlertDialog.Builder dialog = new AlertDialog.Builder
                                                 (TripUnStartActivity.this);
            dialog.setMessage("Need to create a trip!");
            dialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            db.close();
                            finish();
                        }
                    });
            dialog.show();
        }
    }

    /**
     * Update the list in the trip history view for all trips.
     *
     * @param tripList  The trip list to display.
     */
    public void updateTripList(List<Trip> tripList) {
        Log.i(TAG, "update trip");

        TripListContent.clear();
        // Put the trip list to the trip list content
        Iterator tripIterator = tripList.iterator();
        while (tripIterator.hasNext()) {
            Trip curTrip = (Trip) tripIterator.next();
            String tripName = curTrip.getName();
            // Only need to display name in trip history.
            HashMap<String, Object> itemInList = new HashMap<String, Object>();
            itemInList.put("trip", curTrip);
            itemInList.put("tripName", tripName);

            TripListContent.add(itemInList);
        }

        //Display the list of view for trip by SimpleAdapter
        SimpleAdapter listAdapter = new SimpleAdapter(
                TripUnStartActivity.this,
                TripListContent,
                R.layout.trip_history_item,
                new String[]{"trip", "tripName"},
                new int[]{R.id.tripNameTextView}) {
            @Override
            public View getView(int index, View tripListView, ViewGroup parent) {
                tripListView = LinearLayout.inflate(
                        getBaseContext(), R.layout.trip_history_item, null);

                TextView tripName = (TextView) tripListView.findViewById(R.id.tripNameTextView);
                tripName.setText((String)((Map<String, Object>) getItem(index)).get("tripName"));

                Button viewBtn = (Button) tripListView.findViewById(R.id.button6);
                viewBtn.setTag(index);
                viewBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Pass the trip data to start the view activity.
                        int pos = ((Integer) v.getTag()).intValue();
                        Trip t = (Trip)((Map<String, Object>) getItem(pos)).get("trip");
                        Log.i("TAG", String.valueOf(pos));
                        startViewTripActivity(v, t);
                    }
                });
                return tripListView;
            }
        };
        item_list.setAdapter(listAdapter);
    }

    /**
     * Update the list in the trip history view for all trips.
     *
     * @param view  The view for the activity.
     * @param trip  The trip need to display in detail.
     */
    public void startViewTripActivity(View view, Trip trip) {
        Intent intent = new Intent(view.getContext(), ViewTripActivity.class);
        Log.i(TAG, "id " + String.valueOf(trip.getTid()));
        intent.putExtra("My trip", trip);
        view.getContext().startActivity(intent);
    }
}

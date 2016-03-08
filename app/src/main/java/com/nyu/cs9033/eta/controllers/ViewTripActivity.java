package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";

	Button backBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_trip);

		backBtn = (Button) findViewById(R.id.button5);
		backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

		Trip trip = getTrip(getIntent());
        Log.i(TAG, "trip name:"+trip.getName());
		viewTrip(trip);
	}
	
	/**
	 * Create a Trip object via the recent trip that
	 * was passed to TripViewer via an Intent.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public Trip getTrip(Intent i) {
		return i.getParcelableExtra("My trip");
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {
		TextView textview;
		textview = (TextView)findViewById(R.id.textView1);
		textview.setText(trip.getName());
		textview = (TextView)findViewById(R.id.textView2);
		textview.setText(trip.getLocation());
        textview = (TextView)findViewById(R.id.textView3);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		textview.setText(df.format(trip.getTime()));
        //get friend list
        List<Person> friend = new ArrayList<Person>(trip.getFriendList());

        for (int i = 0; i < friend.size(); i++) {
            Person temp = friend.get(i);
            textview = (TextView)findViewById(R.id.textView4);
            textview.setText(temp.getName());
            textview = (TextView)findViewById(R.id.textView5);
            textview.setText(temp.getCurLocation());
            textview = (TextView)findViewById(R.id.textView6);
            textview.setText(temp.getPhoneNumber());
        }
	}
}

/**
 * Main Activity for app. The start point.
 *
 * @author      Jessica Huang
 * @version     1.1
 */
package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	//private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button tripCreateBtn = (Button) findViewById(R.id.button3);
		tripCreateBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startCreateTripActivity(v);
			}
		});
	}

    /**
     * This method should start the activity responsible for
     * creating a Trip.
     *
     * @param v  The view of activity.
     */
	public void startCreateTripActivity(View v) {
		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivityForResult(intent, 1);
	}

    /**
     * This method should start the activity responsible for
     * viewing a Trip.
     *
     * @param view  The view of activity.
     */
	public void startTripHistoryActivity(View view) {
		Intent intent = new Intent(this, TripHistoryActivity.class);
		startActivity(intent);
	}

}


package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	Trip trip;
	
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
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity(View v) {
		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivityForResult(intent, 1);
	}
	
	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	public void startViewTripActivity(View v) {
		if (trip != null) {
			Intent intent = new Intent(this, ViewTripActivity.class);
			intent.putExtra("My trip", trip);
			startActivity(intent);
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setMessage("Need to create a trip!");
			dialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
						}
					});
			dialog.show();
		}
	}
	
	/**
	 * Receive result from CreateTripActivity here.
	 * Can be used to save instance of Trip object
	 * which can be viewed in the ViewTripActivity.
	 * 
	 * Note: This method will be called when a Trip
	 * object is returned to the main activity. 
	 * Remember that the Trip will not be returned as
	 * a Trip object; it will be in the persisted
	 * Parcelable form. The actual Trip object should
	 * be created and saved in a variable for future
	 * use, i.e. to view the trip.
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			trip = data.getParcelableExtra("My trip");
		}
	}
}

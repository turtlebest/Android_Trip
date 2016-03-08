package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
	EditText tripName , tripLocation, friendName, friendLocation, friendPhone;
	DatePicker datePicker;
	TimePicker timePicker;
	Button createButton, cancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_trip);

		tripName = (EditText) findViewById(R.id.editText1);
		tripLocation = (EditText)  findViewById(R.id.editText2);
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		friendName = (EditText)  findViewById(R.id.editText4);
		friendLocation = (EditText)  findViewById(R.id.editText5);
		friendPhone = (EditText)  findViewById(R.id.editText6);

		createButton = (Button) findViewById(R.id.button);
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Trip trip = createTrip(v);
				if (saveTrip(trip)) {
					finish();
				}
			}
		});
		cancelButton = (Button) findViewById(R.id.button2);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancelTrip(v);
			}
		});
	}
	
	/**
	 * This method should be used to
	 * instantiate a Trip model object.
	 * 
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip(View v) {
		String trip_name = tripName.getText().toString();
		String trip_Location = tripLocation.getText().toString();
		String friend_name = friendName.getText().toString();
		String friend_Location = friendLocation.getText().toString();
		String friend_Phone = friendPhone.getText().toString();

        //Transfer date picker and time picker to Date type
		Calendar calendar = new GregorianCalendar(datePicker.getYear(),
				datePicker.getMonth(),
				datePicker.getDayOfMonth(),
				timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		Date trip_Time = calendar.getTime();

		//Add person object into Trip.
		Person friend = new Person(friend_name, friend_Location, friend_Phone);
		List<Person> fList = new ArrayList<Person>();
		fList.add(friend);

		Log.i(TAG, "friend phone:" + friend.getPhoneNumber());
        Log.i(TAG, "friend phone:" + friend_Phone);

		if (trip_name.length() == 0 || trip_Location.length() == 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(CreateTripActivity.this);
			dialog.setMessage("Please fill out trip time and trip location");
			dialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
						}
					});
			dialog.show();
			return null;
		}

		return new Trip(trip_name, trip_Location, trip_Time, fList);
	}

	/**
	 * For HW2 you should treat this method as a 
	 * way of sending the Trip data back to the
	 * main Activity.
	 * 
	 * Note: If you call finish() here the Activity 
	 * will end and pass an Intent back to the
	 * previous Activity using setResult().
	 * 
	 * @return whether the Trip was successfully 
	 * saved.
	 */
	public boolean saveTrip(Trip trip) {
		if (trip != null) {
			Intent tripInfo = new Intent(this, MainActivity.class);
			tripInfo.putExtra("My trip", trip);
			setResult(RESULT_OK, tripInfo);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method should be used when a
	 * user wants to cancel the creation of
	 * a Trip.
	 * 
	 * Note: You most likely want to call this
	 * if your activity dies during the process
	 * of a trip creation or if a cancel/back
	 * button event occurs. Should return to
	 * the previous activity without a result
	 * using finish() and setResult().
	 */
	public void cancelTrip(View v) {
		finish();
	}
}

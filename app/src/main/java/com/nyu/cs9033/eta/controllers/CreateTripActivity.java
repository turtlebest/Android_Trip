/**
 * The activity for creating the trip.
 *
 * @author      Jessica Huang
 * @version     1.1
 */
package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Location;
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
import android.provider.ContactsContract;
import android.net.Uri;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

public class CreateTripActivity extends Activity {
	private static final String TAG = "CreateTripActivity";
    // View item
	EditText tripName , tripLocationPlace, tripLocationState, tripLocationType, friendName;
	DatePicker datePicker;
	TimePicker timePicker;
	Button createButton, cancelButton, addButton, locationButton;
	Uri locationUri;
    // Contact query
	public String contact_name = " ";
	private Uri uriContact;
	private String contactID;// contacts unique ID
    // Trip attributes
    public List<Person> personList = new ArrayList<Person>();
    public Location tripLoc;

	static final int PICK_CONTACT = 1;
	static final int PICK_LOCATION = 2;

	private LocalDB db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_trip);

		tripName = (EditText) findViewById(R.id.editText1);
		tripLocationPlace = (EditText)  findViewById(R.id.editText2);
		tripLocationState = (EditText)  findViewById(R.id.editText3);
		tripLocationType = (EditText)  findViewById(R.id.editText8);
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		friendName = (EditText)  findViewById(R.id.editText4);
        // Use HW3API to get location
		locationButton = (Button) findViewById(R.id.button_get_location);
		locationUri = Uri.parse("location://com.example.nyu.hw3api");
		locationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                if (tripLocationPlace.getText().toString().equals("")||
                        tripLocationState.getText().toString().equals("")||
                        tripLocationType.getText().toString().equals("")
                        ) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CreateTripActivity.this);
                    dialog.setMessage("Need to fill out all items!");
                    dialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                }
                            });
                    dialog.show();
                    return;
                }

				Intent intent= new Intent(Intent.ACTION_VIEW,  locationUri);
				// String extra: key “searchVal”, value “<location>::<location_type>”.
				String inputSearch = tripLocationPlace.getText().toString() + ", " +
						             tripLocationState.getText().toString() + "::" +
						             tripLocationType.getText().toString();
				intent.putExtra("searchVal",inputSearch);
				// String extra: key “searchVal”, value “<location >::<location_type>”.
				startActivityForResult(intent, PICK_LOCATION);
			}
		});

		addButton = (Button) findViewById(R.id.button_add_friend);
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT);
			}
		});

		createButton = (Button) findViewById(R.id.button);
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Trip trip = createTrip(v);
				if (saveTrip(trip)) {
                    db.close();
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

		db = new LocalDB(getApplicationContext());
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		switch (reqCode) {
			case (PICK_CONTACT) : {
				if (resultCode == Activity.RESULT_OK) {
					uriContact = data.getData();

					String name = retrieveContactName();
					String phoneNumber = retrieveContactNumber();

					contact_name += name + " ; ";
					friendName.setText(contact_name);

					Person p = new Person(new Long(0), name, null, phoneNumber);
					personList.add(p);
				}
				break;
			}
			case (PICK_LOCATION) : {
                if (resultCode == Activity.RESULT_FIRST_USER) {
                    Log.i(TAG, "in in in");
                    ArrayList<String> locations = data.getStringArrayListExtra("retVal");
                    tripLocationPlace.setText(locations.get(0));
                    Log.i(TAG, String.valueOf(locations.size()));

                    tripLoc = new Location(locations.get(0), locations.get(1),
                            locations.get(2), locations.get(3));
                }
                break;
			}
		}
	}

    /**
     * Get the phone number from contact
     *
	 * @return The phone of the contact
     */
	private String retrieveContactNumber() {
		String contactNumber = null;

		// getting contacts ID
		Cursor cursorID = getContentResolver().query(uriContact,
				new String[]{ContactsContract.Contacts._ID},
				null, null, null);

		if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
		}

		cursorID.close();

		Log.d(TAG, "Contact ID: " + contactID);

		// Using the contact ID now we will get contact phone number
		Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
						ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
						ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

				new String[]{contactID},
				null);

		if (cursorPhone.moveToFirst()) {
			contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
		}

		cursorPhone.close();

		Log.d(TAG, "Contact Phone Number: " + contactNumber);
		return contactNumber;
	}

    /**
     * Get the name from contact
	 *
     * @return The name of the contact
     */
	private String retrieveContactName() {

		String contactName = null;

		// querying contact data store
		Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
		}

		cursor.close();

		Log.d(TAG, "Contact Name: " + contactName);
		return contactName;
	}
	
	/**
	 * This method should be used to instantiate a Trip model object.
	 * 
	 * @return The Trip as represented by the View.
	 */
	public Trip createTrip(View v) {
		String trip_name = tripName.getText().toString();
		String trip_Location = tripLocationPlace.getText().toString();

        //Transfer date picker and time picker to Date type
		Calendar calendar = new GregorianCalendar(datePicker.getYear(),
				datePicker.getMonth(),
				datePicker.getDayOfMonth(),
				timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		Date trip_Time = calendar.getTime();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String t_time = df.format(trip_Time);

		//Add person object into Trip.
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

        Integer n = personList.size();

        Log.i(TAG, n.toString());

		return new Trip(new Long(0), trip_name, tripLoc, t_time, personList);
	}

	/**
	 * Save trip to the database
	 * 
	 * @param trip The created trip need
	 *             to be saved.
	 */
	public boolean saveTrip(Trip trip) {
		if (trip != null) {
            db.insertTrip(trip);
			Intent tripInfo = new Intent(this, MainActivity.class);
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
	 */
	public void cancelTrip(View v) {
		finish();
	}
}


/**
 * View trip activity for trip detail.
 *
 * @author      Jessica Huang
 * @version     1.1
 */
package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ViewTripActivity extends Activity {
	private static final String TAG = "ViewTripActivity";
    // View items
	public ListView friend_item_list;
	ArrayList<HashMap<String, Object>> FriendListContent
			= new ArrayList<HashMap<String, Object>>();
	Button backBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_trip);

		friend_item_list = (ListView) findViewById(R.id.friendListView);

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
	 *          the most recent trip data.
	 * 
	 * @return  The Trip that was most recently
	 *          passed to TripViewer, or null if there
	 *          is none.
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
		textview.setText(trip.getLocation().getName());
        textview = (TextView)findViewById(R.id.textView3);
		textview.setText(trip.getTime());
        //get friend list
        List<Person> friend = new ArrayList<Person>(trip.getFriendList());

		updateFriendList(friend);
	}

    /**
     * Update the friend list in the trip view for the trip.
     *
     * @param friendList  The friend list to display.
     */
	public void updateFriendList(List<Person> friendList) {
		Log.i(TAG, "update trip");

		FriendListContent.clear();
        // Put the trip list to the trip list content
		Iterator friendIterator = friendList.iterator();
		while (friendIterator.hasNext()) {
			Person curFri = (Person) friendIterator.next();
			String friendName = curFri.getName();
			String friendPhone = curFri.getPhoneNumber();

			HashMap<String, Object> itemInList = new HashMap<String, Object>();
			itemInList.put("friendName", friendName);
			itemInList.put("friendPhone", friendPhone);

			FriendListContent.add(itemInList);
		}

		//Display the list of view for temperature by SimpleAdapter
		SimpleAdapter listAdapter = new SimpleAdapter(
				ViewTripActivity.this,
				FriendListContent,
				R.layout.view_trip_friend_item,
				new String[]{"friendName","friendPhone"},
				new int[]{R.id.friendNameTextView,  R.id.friendPhoneTextView}) {
			@Override
			public View getView(int index, View friendListView, ViewGroup parent) {
				friendListView = LinearLayout.inflate(
						getBaseContext(), R.layout.view_trip_friend_item, null);

				TextView friendName = (TextView) friendListView.findViewById
                        (R.id.friendNameTextView);
                friendName.setText((String) ((Map<String, Object>) getItem(index)).
                        get("friendName"));
				TextView friendPhone = (TextView) friendListView.findViewById
                        (R.id.friendPhoneTextView);
				friendPhone.setText((String) ((Map<String, Object>) getItem(index)).
                        get("friendPhone"));

				return friendListView;
			}
		};
		friend_item_list.setAdapter(listAdapter);
	}
}

package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main Activity for app. The start point.
 *
 * @author      Jessica Huang
 * @version     1.2
 */
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	String distanceLeft;
	String timeLeft;
	String people;

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

		Button tripLocationBtn = (Button) findViewById(R.id.button7);
		tripLocationBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                showFriendStatus();
			}
		});

		Button CurrentTripInfoBtn = (Button) findViewById(R.id.button5);
		CurrentTripInfoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                if (intent.getStringExtra("trip_id") != null) {
                    long trip_id = Integer.parseInt(intent.getStringExtra("trip_id"));
                    LocalDB db = new LocalDB(getApplicationContext());
                    Trip trip = db.getTrip(trip_id);
                    db.close();
                    startViewTripActivity(v, trip);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Need to start a trip!");
                    dialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                }
                            });
                    dialog.show();
                }
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
	 * Start view trip activity to view the current trip.
	 *
	 * @param view  The view of activity.
	 * @param trip  The current trip.
	 */
	public void startViewTripActivity(View view, Trip trip) {
		Intent intent = new Intent(view.getContext(), ViewTripActivity.class);
		Log.i(TAG, "id " + String.valueOf(trip.getTid()));
		intent.putExtra("My trip", trip);
		intent.putExtra("currentTrip", "true");
		view.getContext().startActivity(intent);
	}

    /**
     * This method should start the activity responsible for
     * viewing trips.
     *
     * @param view  The view of activity.
     */
	public void startViewAllTripActivity(View view) {
		Intent intent = new Intent(this, TripViewAllActivity.class);
		startActivity(intent);
	}

    /**
     * Display the information from API for friend status in current trip.
     */
	public void showFriendStatus() {
		if (distanceLeft == null) {
            // If there is no data for friend status.
            Intent intent = getIntent();
            if (intent.getStringExtra("trip_id") != null) {
                // There is current trip, connect to API.
                connectToAPI();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setMessage("Need to start a trip!");
				dialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						});
				dialog.show();
			}
		} else {
            // Display the information.
			String resultString = distanceLeft.substring(1, distanceLeft.length() - 1);
			ArrayList<String> distanceLeftList = new ArrayList<String>(
                    Arrays.asList(resultString.split(",")));
			String resultString1 = timeLeft.substring(1, timeLeft.length() - 1);
			ArrayList<String> timeLeftList = new ArrayList<String>(
                    Arrays.asList(resultString1.split(",")));
			String resultString2 = people.substring(1, people.length() - 1);

            ArrayList<String> peopleList = new ArrayList<String>(
                    Arrays.asList(resultString2.split(",")));
			String show = "";
			for (int i = 0; i < peopleList.size(); i++) {
				show += peopleList.get(i) +
                        " will arrived in " +
                        String.valueOf(timeLeftList.get(i)) +
						"seconds. Distance Left: " +
                        String.valueOf(distanceLeftList.get(i)) +
						"miles" + "\n";
			}

			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("Current Trip Info");
			dialog.setMessage(show);
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
     * Start the service to connect to the API.
     */
    private void connectToAPI() {
        // Check if the internet is connected.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get the information from API.
            viewTripInfo();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Network need to be connected!");
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
     * Call the API for trip information.
     */
	public void viewTripInfo() {
		String url_string = "http://cs9033-homework.appspot.com/";
		new CallAPI().execute(url_string);
	}

    /**
     * Transfer the data to json format to call API.
     *
     * @return json format to call the API.
     */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("command", "TRIP_STATUS");
			Intent intent = getIntent();
			long tid = 0;
			if (intent.getStringExtra("trip_id") != null) {
				tid = Integer.parseInt(intent.getStringExtra("trip_id"));
			}
			json.put("trip_id", tid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

    /**
     * Inner class to call the API service.
     *
     * @author      Jessica Huang
     * @version     1.0
     */
	private class CallAPI extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				updateTrip(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "convertStreamToString(in)";
		}

		@Override
		protected void onPostExecute(String result) {
            showFriendStatus();
		}

        /**
         * update trip information by calling API service.
         *
         * @param url  The API service website.
         * @return     result from API.
         * @throws     IOException
         */
		private String updateTrip(String url) throws IOException {
			JSONObject jsonObjSend;
			String resultString = "no trip";
			try {
                // Connect to API service.
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
					JSONObject jsonObj_Res = new JSONObject(resultString);
					distanceLeft = jsonObj_Res.getString("distance_left");
					timeLeft = jsonObj_Res.getString("time_left");
					people = jsonObj_Res.getString("people");
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
		private String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
}

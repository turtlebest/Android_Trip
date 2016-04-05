/**
 * LocalDB is the class to deal with sql query for
 * the sql lite database in the phone
 *
 * @author      Jessica Huang
 * @version     1.0
 */
package com.nyu.cs9033.eta.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.Location;

public class LocalDB {
    private static final String TAG = "LocalDB";
    // Trip table
    public static final String TABLE_TRIP = "trip";
    public static final String COLUMN_TRIP_ID = "_id";
    public static final String COLUMN_TRIP_NAME = "trip_Name";
    public static final String COLUMN_TRIP_LOC_ID = "trip_Location_id";
    public static final String COLUMN_TRIP_TIME = "trip_Time";
    // Person table
    public static final String TABLE_PERSON = "person";
    public static final String COLUMN_PER_ID = "_id";
    public static final String COLUMN_PER_NAME = "name";
    public static final String COLUMN_PER_PHONE = "phone";
    // Attend_Trip table
    public static final String TABLE_ATTEND = "attend_trip";
    public static final String COLUMN_ATT_PERSON_ID = "p_id";
    public static final String COLUMN_ATT_TRIP_ID = "t_id";
    // Location table
    public static final String TABLE_LOCATION = "location";
    public static final String COLUMN_LOC_ID = "_id";
    public static final String COLUMN_LOC_LAT = "latitude";
    public static final String COLUMN_LOC_LONG = "longitude";
    public static final String COLUMN_LOC_ADDRESS = "address";
    public static final String COLUMN_LOC_NAME = "name";

    public static final String CREATE_TRIP_TABLE = "create table "  + TABLE_TRIP + "("
            + COLUMN_TRIP_ID + " integer primary key autoincrement, "
            + COLUMN_TRIP_NAME + " text, "
            + COLUMN_TRIP_LOC_ID + " integer references location(_id), "
            + COLUMN_TRIP_TIME + " text)";

    public static final String CREATE_PERSON_TABLE = "create table " + TABLE_PERSON + "("
            + COLUMN_PER_ID + " integer primary key autoincrement, "
            + COLUMN_PER_PHONE + " text, "
            + COLUMN_PER_NAME + " text)";

    public static final String CREATE_ATTEND_TABLE = "create table " + TABLE_ATTEND + "("
            + COLUMN_ATT_PERSON_ID + " integer references person(phone), "
            + COLUMN_ATT_TRIP_ID + " integer references trip(_id))";

    public static final String CREATE_LOCATION_TABLE = "create table " + TABLE_LOCATION + "("
            + COLUMN_LOC_ID + " integer primary key autoincrement, "
            + COLUMN_LOC_NAME + " text, "
            + COLUMN_LOC_ADDRESS + " text, "
            + COLUMN_LOC_LAT + " real, "
            + COLUMN_LOC_LONG + " real)";

    private SQLiteDatabase db;

    /**
     * Class constructor specifying the context of database.
     */
    public LocalDB(Context context) {
        db = TripDatabaseHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    /**
     * Insert the trip the the database.
     *
     * @param trip  The Trip model used to populate the View.
     * @return      <code>true</code> insert the trip successfully;
     *              <code>false</code> insert the trip fail.
     */
    public Boolean insertTrip(Trip trip) {
        Log.i(TAG, "insertTrip " + trip.getName());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRIP_NAME, trip.getName());
        // Get the id of location
        long lid = insertLocation(trip.getLocation());
        cv.put(COLUMN_TRIP_LOC_ID, lid);
        cv.put(COLUMN_TRIP_TIME, trip.getTime());
        // return id of new trip
        long tid = db.insert(TABLE_TRIP, null, cv);
        trip.setTid(tid);

        // Save the person for trip into the database.
        List<Person> list = trip.getFriendList();
        for (int i = 0; i < list.size(); i++) {
            Person p = list.get(i);
            // Get the id from person
            long pid = insertPerson(p);
            Log.i(TAG, "insertTrip " + String.valueOf(p.getPid()));
            Log.i(TAG, "insertTrip " + p.getName());
            // Insert the pid and tid into the Attend_Trip table.
            insertAttend(pid, tid);
        }
        return true;
    }

    /**
     * Insert the trip attendance data into Attend_Trip table
     *
     * @param pid  The person id to.
     * @param tid  The Trip id.
     * @return      <code>true</code> insert the data successfully;
     *              <code>false</code> insert the data fail.
     */
    public boolean insertAttend(long pid, long tid) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ATT_PERSON_ID, pid);
        cv.put(COLUMN_ATT_TRIP_ID, tid);

        db.insert(TABLE_ATTEND, null, cv);

        Log.i(TAG, "insertAttend " + String.valueOf(pid));

        return true;
    }

    /**
     * Insert the location into Location table.
     *
     * @param loc  The location object.
     * @return     The id of the insert location.
     */
    public long insertLocation(Location loc) {
        Cursor cursor = db.query(TABLE_LOCATION, new String[]{COLUMN_LOC_ID},
                COLUMN_LOC_NAME + " = " + '"' + loc.getName() + '"', null, null, null, null);

        long id;

        if (!cursor.moveToNext()) {
            // Insert new data if there is not one in database.
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_LOC_NAME, loc.getName());
            cv.put(COLUMN_LOC_ADDRESS, loc.getAddress());
            cv.put(COLUMN_LOC_LAT, loc.getLatitude());
            cv.put(COLUMN_LOC_LONG, loc.getLongitude());

            id = db.insert(TABLE_LOCATION, null, cv);
            Log.i(TAG, "insertLocation " + String.valueOf(id));

        } else {
            id = cursor.getLong(0);
        }
        cursor.close();

        return id;
    }

    /**
     * Populate the View using a Trip model.
     *
     * @param person  The person object.
     * @return      The id from person
     */
    public long insertPerson(Person person) {
        Cursor cursor = db.query(TABLE_PERSON, new String[]{COLUMN_PER_ID},
                COLUMN_PER_PHONE + " = " + '"' + person.getPhoneNumber() + '"',
                null, null, null, null);
        Log.i(TAG, "insertPerson-phone " + person.getPhoneNumber());
        long pid;

        if (!cursor.moveToNext()) {
            // Insert new data if there is not one in database.
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_PER_NAME, person.getName());
            cv.put(COLUMN_PER_PHONE, person.getPhoneNumber());

            pid = db.insert(TABLE_PERSON, null, cv);
            Log.i(TAG, "insertPerson " + String.valueOf(pid));
            person.setPid(pid);
        } else {
            pid = cursor.getLong(0);
            Log.i(TAG, "insertPerson:find " + String.valueOf(pid));
        }
        cursor.close();

        return pid;
    }

    /**
     * Get all the trips.
     *
     * @return The trip list.
     */
    public List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<Trip>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Long tid = cursor.getLong(0);
            String name = cursor.getString(1);
            String time = cursor.getString(3);
            long lid = cursor.getLong(2);

            Location loc = getLocation(lid);
            List<Person> friendList = getTripAttend(tid);
            Trip trip = new Trip(tid, name, loc, time, friendList);
            tripList.add(trip);
        }
        cursor.close();

        return tripList;
    }

    /**
     * Get person for attending the certain trip.
     *
     * @param tid  The Trip id to search for.
     * @return     The list of person who attend the trip.
     */
    public List<Person> getTripAttend(Long tid) {
        List<Person> personList = new ArrayList<Person>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_ATTEND + " where " +
                        COLUMN_ATT_TRIP_ID + " = ?", new String[] {tid.toString()});
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Long pid = cursor.getLong(0);
            Person person = getPerson(pid);
            if (person != null) {
                personList.add(person);
            }
        }
        cursor.close();

        return personList;
    }

    /**
     * Get the person object by person id.
     *
     * @param id  Person id.
     * @return    The person object.
     */
    public Person getPerson(long id) {
        // for return result
        Person person = null;
        // ID
        String where = COLUMN_PER_ID + "=" + id;
        // search
        Cursor result = db.query(TABLE_PERSON, null, where, null, null, null, null, null);
        // if there is a data
        if (result.moveToFirst()) {
            // get on of the data
            long pid = result.getLong(0);
            String name = result.getString(2);
            Location loc = new Location(" "," "," "," ");
            String phone = result.getString(1);

            person = new Person(pid, name, loc, phone);
        }
        result.close();

        return person;
    }

    /**
     * Get the location object by location id.
     *
     * @param id  Location id.
     * @return    The location object.
     */
    public Location getLocation(long id) {
        // for return result
        Location loc = null;
        // ID
        String where = COLUMN_LOC_ID + "=" + id;

        // search
        Cursor result = db.query(TABLE_LOCATION, null, where, null, null, null, null, null);
        // if there is a data
        if (result.moveToFirst()) {
            // get on of the data
            String name = result.getString(1);
            String address = result.getString(2);
            String lat = result.getString(3);
            String lon = result.getString(4);

            loc = new Location(name, address, lat, lon);
            Log.i(TAG, result.getString(1));
        }
        result.close();
        return loc;
    }
}

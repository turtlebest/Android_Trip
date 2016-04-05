/**
 * Trip database helper for creating the database
 * for the trip app.
 *
 * @author      Jessica Huang
 * @version     1.0
 */
package com.nyu.cs9033.eta.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TripDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 16;
    private static SQLiteDatabase db;
    private static final String DATABASE_NAME = "trips16";

    /**
     * Class constructor specifying the context of database, the name
     * the factory and the version.
     */
    public TripDatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Get the database object for local DB.
     *
     * @param context  The context for database.
     */
    public static SQLiteDatabase getDatabase(Context context) {
        if (db == null || !db.isOpen()) {
            // If the data base is not existed or not open,
            // create a new one.
            db = new TripDatabaseHelper(context, DATABASE_NAME,
                    null, DATABASE_VERSION).getWritableDatabase();
        }

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the tables
        db.execSQL(LocalDB.CREATE_TRIP_TABLE);
        db.execSQL(LocalDB.CREATE_PERSON_TABLE);
        db.execSQL(LocalDB.CREATE_LOCATION_TABLE);
        db.execSQL(LocalDB.CREATE_ATTEND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {

        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + LocalDB.TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + LocalDB.TABLE_PERSON);
        db.execSQL("DROP TABLE IF EXISTS " + LocalDB.TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + LocalDB.TABLE_ATTEND);
        // create tables again
        onCreate(db);
    }
}

package com.conceptappsworld.habittracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.conceptappsworld.habittracker.data.HabitTrackerContract.HabitEntry;

/**
 * Database helper for HabitTracker app. Manages database creation and version management.
 */

public class HabitTrackerDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HabitTrackerDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "habittracker.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link HabitTrackerDbHelper}.
     *
     * @param context of the app
     */
    public HabitTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the habits table
        String SQL_CREATE_HABIT_TABLE =  "CREATE TABLE " + HabitEntry.TABLE_NAME + " ("
                + HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HabitEntry.COLUMN_PERSON_NAME + " TEXT NOT NULL, "
                + HabitEntry.COLUMN_PERSON_HABIT + " TEXT, "
                + HabitEntry.COLUMN_PERSON_GENDER + " INTEGER NOT NULL, "
                + HabitEntry.COLUMN_HABIT_FREQUENCY + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_HABIT_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

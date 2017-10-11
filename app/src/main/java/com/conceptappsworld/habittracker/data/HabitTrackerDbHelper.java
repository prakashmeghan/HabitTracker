package com.conceptappsworld.habittracker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.conceptappsworld.habittracker.data.HabitTrackerContract.HabitEntry;
import com.conceptappsworld.habittracker.model.Habit;

/**
 * Database helper for HabitTracker app. Manages database creation and version management.
 */

public class HabitTrackerDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HabitTrackerDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
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
        String SQL_CREATE_HABIT_TABLE = "CREATE TABLE " + HabitEntry.TABLE_NAME + " ("
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

    public Cursor queryHabit(int habitId){
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_PERSON_NAME,
                HabitEntry.COLUMN_PERSON_HABIT,
                HabitEntry.COLUMN_PERSON_GENDER,
                HabitEntry.COLUMN_HABIT_FREQUENCY};

        // Perform a query on the habits table
        cursor = db.query(
                HabitEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                HabitEntry._ID + "=?",                  // The columns for the WHERE clause
                new String[]{String.valueOf(habitId)},                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        return cursor;
    }

    public Habit getHabit(int habitId) {
        Habit habit = null;

        Cursor cursor = queryHabit(habitId);

        try {
            int idColumnIndex = cursor.getColumnIndex(HabitEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_NAME);
            int habitColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_HABIT);
            int genderColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_GENDER);
            int freqColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_FREQUENCY);

            if (cursor.moveToFirst()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentHabit = cursor.getString(habitColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentFreq = cursor.getInt(freqColumnIndex);

                habit = new Habit(currentID, currentName, currentHabit, currentGender, currentFreq);
            }
        } finally {
            cursor.close();
        }
        return habit;
    }

    public boolean deleteHabit(int habitId) {
        boolean isDeleted = false;
        SQLiteDatabase db = getWritableDatabase();

        if (habitId == 0) {
            isDeleted = db.delete(HabitEntry.TABLE_NAME,
                    null,
                    null) > 0;
        } else {
            isDeleted = db.delete(HabitEntry.TABLE_NAME,
                    HabitEntry._ID + "=?",
                    new String[]{String.valueOf(habitId)}) > 0;
        }

        return isDeleted;
    }
}

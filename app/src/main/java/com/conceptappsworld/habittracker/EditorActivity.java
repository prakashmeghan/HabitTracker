/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.conceptappsworld.habittracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.conceptappsworld.habittracker.data.HabitTrackerContract.HabitEntry;
import com.conceptappsworld.habittracker.data.HabitTrackerDbHelper;
import com.conceptappsworld.habittracker.model.Habit;
import com.conceptappsworld.habittracker.util.ConstantUtil;

/**
 * Allows user to create a new habit or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the person's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the person's habit
     */
    private EditText mHabitEditText;

    /**
     * EditText field to enter the habit's frequency
     */
    private EditText mFreqEditText;

    /**
     * EditText field to enter the person's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the person. The possible valid values are in the HabitTrackerContract.java file:
     * {@link HabitEntry#GENDER_UNKNOWN}, {@link HabitEntry#GENDER_MALE}, or
     * {@link HabitEntry#GENDER_FEMALE}.
     */
    private int mGender = HabitEntry.GENDER_UNKNOWN;

    private int habitId;

    HabitTrackerDbHelper mDbHelper;
    SQLiteDatabase db;
    int selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Create database helper
        mDbHelper = new HabitTrackerDbHelper(this);

        // Gets the database in write mode
        db = mDbHelper.getWritableDatabase();

        getValueFromIntent();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_person_name);
        mHabitEditText = (EditText) findViewById(R.id.edit_person_habit);
        mFreqEditText = (EditText) findViewById(R.id.edit_habit_freq);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        if (habitId != 0) {
            Habit habit = mDbHelper.getHabit(habitId);
            if (habit != null) {
                mNameEditText.setText(habit.getPersonName());
                mHabitEditText.setText(habit.getHabitName());
                mFreqEditText.setText(String.valueOf(habit.getHabitFrequency()));
                selectedGender = habit.getPersonGender();
            }
        }
        setupSpinner();
    }

    private void getValueFromIntent() {
        habitId = getIntent().getIntExtra(ConstantUtil.INTENT_EXTRA_HABIT_ID, 0);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the person.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        if (habitId != 0) {
            mGenderSpinner.setSelection(selectedGender);
        }

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = HabitEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = HabitEntry.GENDER_FEMALE;
                    } else {
                        mGender = HabitEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = HabitEntry.GENDER_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save new habit into database.
     */
    private void insertHabit() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String habitString = mHabitEditText.getText().toString().trim();
        String freqString = mFreqEditText.getText().toString().trim();
        int freq = Integer.parseInt(freqString);


        // Create a ContentValues object where column names are the keys,
        // and person attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_PERSON_NAME, nameString);
        values.put(HabitEntry.COLUMN_PERSON_HABIT, habitString);
        values.put(HabitEntry.COLUMN_PERSON_GENDER, mGender);
        values.put(HabitEntry.COLUMN_HABIT_FREQUENCY, freq);

        // Insert a new row for habit in the database, returning the ID of that new row.
        long newRowId = db.insert(HabitEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving habit", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Habit saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get user input from editor and update existing habit into database.
     */
    private void updateHabit(int habitId) {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String habitString = mHabitEditText.getText().toString().trim();
        String freqString = mFreqEditText.getText().toString().trim();
        int freq = Integer.parseInt(freqString);


        // Create a ContentValues object where column names are the keys,
        // and person attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_PERSON_NAME, nameString);
        values.put(HabitEntry.COLUMN_PERSON_HABIT, habitString);
        values.put(HabitEntry.COLUMN_PERSON_GENDER, mGender);
        values.put(HabitEntry.COLUMN_HABIT_FREQUENCY, freq);

        // update a existing row for habit in the database.
        int rowId = db.update(HabitEntry.TABLE_NAME, values, HabitEntry._ID + "=?",
                new String[]{String.valueOf(habitId)});

        // Show a toast message depending on whether or not the insertion was successful
        if (rowId == 0) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with updating habit", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Habit updated with row id: " + rowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save habit to database
                if (habitId != 0) {
                    updateHabit(habitId);
                } else {
                    insertHabit();
                }

                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if (habitId != 0) {
                    deleteHabit(habitId);
                } else {
                    Toast.makeText(this, "No habit found to delete for now.", Toast.LENGTH_SHORT).show();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteHabit(int habitId) {
        if (mDbHelper.deleteHabit(habitId)) {
            Toast.makeText(this, "Habit deleted successfully.", Toast.LENGTH_SHORT).show();
            // Exit activity
            finish();
        } else {
            Toast.makeText(this, "Habit couldn't deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
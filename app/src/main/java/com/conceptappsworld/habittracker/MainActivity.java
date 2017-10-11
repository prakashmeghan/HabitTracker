package com.conceptappsworld.habittracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conceptappsworld.habittracker.adapter.HabitAdapter;
import com.conceptappsworld.habittracker.data.HabitTrackerContract.HabitEntry;
import com.conceptappsworld.habittracker.data.HabitTrackerDbHelper;
import com.conceptappsworld.habittracker.model.Habit;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     * Database helper that will provide us access to the database
     */
    private HabitTrackerDbHelper mDbHelper;

    private RecyclerView rvHabit;
    HabitAdapter habitAdapter;
    private ArrayList<Habit> alHabit;
    private TextView tvEmpty;
    private LinearLayout llHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new HabitTrackerDbHelper(this);

        findViewByIds();
        rvHabitSetup();
    }

    private void findViewByIds() {
        rvHabit = (RecyclerView) findViewById(R.id.rv_habits);
        TextView tvId = (TextView) findViewById(R.id.tv_h_person_id);
        TextView tvName = (TextView) findViewById(R.id.tv_h_person_name);
        TextView tvHabit = (TextView) findViewById(R.id.tv_h_person_habit);
        TextView tvGender = (TextView) findViewById(R.id.tv_h_gender);
        TextView tvFrequency = (TextView) findViewById(R.id.tv_h_freq);

        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        llHeading = (LinearLayout) findViewById(R.id.ll_heading);

        tvId.setText(getResources().getString(R.string.id));
        tvName.setText(getResources().getString(R.string.hint_person_name));
        tvHabit.setText(getResources().getString(R.string.hint_person_habit));
        tvGender.setText(getResources().getString(R.string.gender));
        tvFrequency.setText(getResources().getString(R.string.frequency));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHabit.setLayoutManager(linearLayoutManager);
        rvHabit.setItemAnimator(new DefaultItemAnimator());
        rvHabit.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
    }

    private void rvHabitSetup() {
        alHabit = new ArrayList<Habit>();

        // Create a new {@link ArrayAdapter} of habit
        habitAdapter = new HabitAdapter(alHabit, MainActivity.this);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        rvHabit.setAdapter(habitAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the HabitTracker database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_PERSON_NAME,
                HabitEntry.COLUMN_PERSON_HABIT,
                HabitEntry.COLUMN_PERSON_GENDER,
                HabitEntry.COLUMN_HABIT_FREQUENCY};

        // Perform a query on the habits table
        Cursor cursor = db.query(
                HabitEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        try {
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(HabitEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_NAME);
            int habitColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_HABIT);
            int genderColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_PERSON_GENDER);
            int freqColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_FREQUENCY);

            alHabit.clear();

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {

                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentHabit = cursor.getString(habitColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentFreq = cursor.getInt(freqColumnIndex);

                Habit habit = new Habit(currentID, currentName, currentHabit, currentGender, currentFreq);
                alHabit.add(habit);
            }

            if (alHabit.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                llHeading.setVisibility(View.GONE);
                rvHabit.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                llHeading.setVisibility(View.VISIBLE);
                rvHabit.setVisibility(View.VISIBLE);
            }
            habitAdapter.notifyDataSetChanged();
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded habit data into the database. For debugging purposes only.
     */
    private void insertHabit() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Prakash's habit attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_PERSON_NAME, "Prakash");
        values.put(HabitEntry.COLUMN_PERSON_HABIT, "Coding");
        values.put(HabitEntry.COLUMN_PERSON_GENDER, HabitEntry.GENDER_MALE);
        values.put(HabitEntry.COLUMN_HABIT_FREQUENCY, 100);

        // Insert a new row for Prakash in the database, returning the ID of that new row.
        // The first argument for db.insert() is the habit table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Prakash.
        long newRowId = db.insert(HabitEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertHabit();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                if(alHabit.isEmpty()){
                    Toast.makeText(this, "Please add habits first.", Toast.LENGTH_SHORT).show();
                } else {
                    if (mDbHelper.deleteHabit(0)) {
                        Toast.makeText(this, "All habits deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "All habits couldn't deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

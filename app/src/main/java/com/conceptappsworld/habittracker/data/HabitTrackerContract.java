package com.conceptappsworld.habittracker.data;

import android.provider.BaseColumns;

/**
 * API Contract for the HabitTracker app.
 */
public final class HabitTrackerContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private HabitTrackerContract() {}


    /**
     * Inner class that defines constant values for the HabitTracker database table.
     * Each entry in the table represents a single Habit.
     */
    public static final class HabitEntry implements BaseColumns {

        /** Name of database table for habits */
        public final static String TABLE_NAME = "habits";

        /**
         * Unique ID number for the Habit (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Person.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PERSON_NAME ="person_name";

        /**
         * Habit of the person.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PERSON_HABIT = "person_habit";

        /**
         * Gender of the person.
         *
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PERSON_GENDER = "gender";

        /**
         * Number of times habit do.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_FREQUENCY = "frequency";

        /**
         * Possible values for the gender of the person.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}

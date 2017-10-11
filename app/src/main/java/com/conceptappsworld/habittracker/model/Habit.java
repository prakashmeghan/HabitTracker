package com.conceptappsworld.habittracker.model;

public class Habit {
    private int habitId;
    private String personName;
    private String habitName;
    private int personGender;
    private int habitFrequency;

    public Habit(int _id, String _personName, String _habitName, int _personGender, int _habitFrequency) {
        personName = _personName;
        habitId = _id;
        habitName = _habitName;
        personGender = _personGender;
        habitFrequency = _habitFrequency;
    }

    public int getHabitId() {
        return habitId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getHabitName() {
        return habitName;
    }

    public int getPersonGender() {
        return personGender;
    }

    public int getHabitFrequency() {
        return habitFrequency;
    }
}

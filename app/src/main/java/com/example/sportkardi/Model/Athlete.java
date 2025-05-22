package com.example.sportkardi.Model;

import com.google.firebase.Timestamp;

public class Athlete{
    private String personalId;
    private String name;
    private String height;
    private String weight;
    private String period;
    private String sport;
    private String weeklyWorkout;
    private String sportAge;
    private String sportType;
    private String complaints;
    private String habits;
    private String medicines;
    private Timestamp timestamp;

    public Athlete() {
    }

    public Athlete(String personalId, String name, String height, String weight, String period, String sport, String weeklyWorkout, String sportAge, String sportType, String complaints, String habits, String medicines, Timestamp timestamp) {
        this.personalId = personalId;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.period = period;
        this.sport = sport;
        this.weeklyWorkout = weeklyWorkout;
        this.sportAge = sportAge;
        this.sportType = sportType;
        this.complaints = complaints;
        this.habits = habits;
        this.medicines = medicines;
        this.timestamp = timestamp;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getPeriod() {
        return period;
    }

    public String getSport() {
        return sport;
    }

    public String getWeeklyWorkout() {
        return weeklyWorkout;
    }

    public String getSportAge() {
        return sportAge;
    }

    public String getSportType() {
        return sportType;
    }

    public String getComplaints() {
        return complaints;
    }

    public String getHabits() {
        return habits;
    }

    public String getMedicines() {
        return medicines;
    }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}

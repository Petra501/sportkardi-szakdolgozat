package com.example.sportkardi.Model;

public class TodaysAppointment {
    private String personalId;
    private String day;
    private String time;

    public TodaysAppointment(String personalId, String day, String time) {
        this.personalId = personalId;
        this.day = day;
        this.time = time;
    }

    public TodaysAppointment(String timeRange) {
        this.time = time;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

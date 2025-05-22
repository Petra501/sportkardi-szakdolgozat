package com.example.sportkardi.Model;

public class MyAppointment {
    private String personalId;
    private String day;
    private String time;
    private String message;

    public MyAppointment(String personalId, String day, String time, String message) {
        this.personalId = personalId;
        this.day = day;
        this.time = time;
        this.message = message;
    }

    public MyAppointment(String timeRange) {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

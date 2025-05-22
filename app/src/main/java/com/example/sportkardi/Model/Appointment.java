package com.example.sportkardi.Model;

public class Appointment {
    private String timeRange;
    private String personalId;
    private String day;
    private String message;

    public Appointment(String timeRange) {
        this.timeRange = timeRange;
        this.personalId = "";
        this.day = "";
        this.message = "";
    }

    public Appointment(String timeRange, String personalId, String day, String message) {
        this.timeRange = timeRange;
        this.personalId = personalId;
        this.day = day;
        this.message = message;
    }

    public Appointment() {
    }

    public Appointment(String timeRange, String personalId) {
        this.timeRange = timeRange;
        this.personalId = personalId;
    }

    public String getTimeRange() {
        return timeRange;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}

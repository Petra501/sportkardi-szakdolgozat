package com.example.sportkardi.Model;

import com.google.firebase.Timestamp;

public class BloodPressure {
    private String personalId;
    private int diary;
    private int diastole;
    private int number;
    private int pulse;
    private int systole;
    private Timestamp time;

    public BloodPressure() {
    }

    public BloodPressure(String personalId, int diary, int diastole, int number, int pulse, int systole, Timestamp time) {
        this.personalId = personalId;
        this.diary = diary;
        this.diastole = diastole;
        this.number = number;
        this.pulse = pulse;
        this.systole = systole;
        this.time = time;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public int getDiary() {
        return diary;
    }

    public void setDiary(int diary) {
        this.diary = diary;
    }

    public int getDiastole() {
        return diastole;
    }

    public void setDiastole(int diastole) {
        this.diastole = diastole;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getSystole() {
        return systole;
    }

    public void setSystole(int systole) {
        this.systole = systole;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}

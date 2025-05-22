package com.example.sportkardi.Model;

public class User {
    private String personalId;
    private String name;
    private int birthYear;
    private String phone;
    private String gender;
    private String password;
    private boolean admin;
    private boolean hasAppointmentRequest;

    public User(String personalId, String name, int birthYear, String phone, String gender, String password, boolean admin, boolean hasAppointmentRequest) {
        this.personalId = personalId;
        this.name = name;
        this.birthYear = birthYear;
        this.phone = phone;
        this.gender = gender;
        this.password = password;
        this.admin = admin;
        this.hasAppointmentRequest = hasAppointmentRequest;
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

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isHasAppointmentRequest() {
        return hasAppointmentRequest;
    }

    public void setHasAppointmentRequest(boolean hasAppointmentRequest) {
        this.hasAppointmentRequest = hasAppointmentRequest;
    }
}

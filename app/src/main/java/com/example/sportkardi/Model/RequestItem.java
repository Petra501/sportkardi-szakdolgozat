package com.example.sportkardi.Model;

public class RequestItem {
    private String personalId;
    private String comment;

    public RequestItem(String personalId, String comment) {
        this.personalId = personalId;
        this.comment = comment;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

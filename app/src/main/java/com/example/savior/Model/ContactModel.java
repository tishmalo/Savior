package com.example.savior.Model;

public class ContactModel {
    private String myEmail, contactEmail, userId;

    public ContactModel() {
    }

    public ContactModel(String myEmail, String contactEmail, String userId) {
        this.myEmail = myEmail;
        this.contactEmail = contactEmail;
        this.userId = userId;
    }

    public String getmyEmail() {
        return myEmail;
    }

    public void setmyEmail(String myEmail) {
        this.myEmail = myEmail;
    }

    public String getcontactEmail() {
        return contactEmail;
    }

    public void setcontactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }
}

package com.feed.acro.acrofeedapplication.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
 
    public String name;
    public String email;
    public String contact;
    public String gender;
    public String dob;
    public String enrollmentNumber;
    public String password;
    public String loginType;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }
 
    public User(String name, String email, String contact,
                String gender,String dob, String enrollmentNumber, String password, String loginType) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.gender = gender;
        this.dob = dob;
        this.enrollmentNumber = enrollmentNumber;
        this.password = password;
        this.loginType = loginType;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
package com.feed.acro.acrofeedapplication.models;

/**
 * Created by hp on 24-03-2018.
 */

public class LoginDetails {

    public String userId;
    public String password;
    public int userType;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public LoginDetails() {
    }

    public LoginDetails(String userId, String password, int userType) {
        this.userId = userId;
        this.password = password;
        this.userType = userType;
    }
}

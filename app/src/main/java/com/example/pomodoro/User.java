package com.example.pomodoro;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String UID;
    public String Nickname;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String UID, String Nickname) {
        this.UID = UID;
        this.Nickname = Nickname;
    }

}
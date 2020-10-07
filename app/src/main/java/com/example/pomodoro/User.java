package com.example.pomodoro;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String UID;
    public String Nickname;
    public String LongestStreak;
    public int AveragePomoTime;
    public int TimeChallenge;
    public int ChallengeWin;
    public int LongestChallenge;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.AveragePomoTime=0;
        this.ChallengeWin=0;
        this.TimeChallenge=0;
        this.LongestChallenge=0;

    }

    public User(String UID, String Nickname) {
        this.UID = UID;
        this.Nickname = Nickname;
    }

}
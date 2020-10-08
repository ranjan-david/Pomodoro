package com.example.pomodoro;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String UID;
    public String Nickname;
    public long LongestStreak;
    public long AveragePomoTime;
    public long TimeChallenge;
    public long ChallengeWin;
    public long LongestChallenge;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public String getUID(){
        return this.UID;
    }
    public String getNickname(){
        return this.Nickname;
    }
    public long getLongestStreak(){
        return this.LongestStreak;
    }
    public long getAveragePomoTime(){
        return this.AveragePomoTime;
    }
    public long getTimeChallenge(){
        return this.TimeChallenge;
    }
    public long getChallengeWin(){
        return this.ChallengeWin;
    }
    public long getLongestChallenge(){
        return this.LongestChallenge;
    }

    public void setAveragePomoTime(long averagePomoTime) {
        AveragePomoTime = averagePomoTime;
    }

    public void setChallengeWin(long challengeWin) {
        ChallengeWin = challengeWin;
    }

    public void setLongestChallenge(long longestChallenge) {
        LongestChallenge = longestChallenge;
    }

    public void setLongestStreak(long longestStreak) {
        LongestStreak = longestStreak;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public void setTimeChallenge(long timeChallenge) {
        TimeChallenge = timeChallenge;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
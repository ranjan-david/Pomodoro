package com.example.pomodoro;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Place {

    public String CreatedBy;
    public String PlaceName;
    public double Latitude;
    public double Longitude;

    public Place(){
    }

    public Place(String name) {
        PlaceName = name;
    }

}
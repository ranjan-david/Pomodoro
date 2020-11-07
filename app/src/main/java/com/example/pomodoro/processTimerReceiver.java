package com.example.pomodoro;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TimerTask;

public class processTimerReceiver extends BroadcastReceiver {
    private int timeSpent;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference database;
    String finalPlaceId;

    public processTimerReceiver(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        Log.i("Updating", "Timer");
        finalPlaceId = intent.getExtras().getString("Place");

        //Get the previous time value from the database, update it TO DO: Add more UI reactivity
        database.child("User").child(user.getUid()).child("LocationTimes").child(finalPlaceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null){
                    database.child("User").child(user.getUid()).child("LocationTimes").child(finalPlaceId).setValue(0);
                    timeSpent = 0;
                }else {
                    timeSpent = (Integer) snapshot.getValue(Integer.class);
                }
                Log.i("Retrieved time spent", String.valueOf(timeSpent));
                timeSpent ++;
                Log.i("Updated time spent to:", String.valueOf(timeSpent));
                database.child("User").child(user.getUid()).child("LocationTimes").child(finalPlaceId).setValue(timeSpent);
            } @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });



    }
}
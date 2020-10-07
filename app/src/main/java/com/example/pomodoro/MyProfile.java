package com.example.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity {
    public User currentUser;
    public String x ;
    public DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        //mock ups
        if (this.currentUser==null){
            this.currentUser = new User("Tian","TianHang");
        }
        else {
            System.out.println(this.currentUser.UID);
        }


    }
    @Override
    public void onStart() {
        super.onStart();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Result is "+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");

            }
        });

        System.out.println("Nickname is "+currentUser.Nickname);
        System.out.println("NicknameB is "+x);

    }
}
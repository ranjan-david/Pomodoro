package com.example.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;

public class MyProfile extends AppCompatActivity {
    public User currentUser;
    public DatabaseReference myRef;

    private TextView LongestStreakData;
    private TextView AveragePomoTimeData;
    private TextView TimeChallengedData;
    private TextView ChallengeWonData;
    private TextView LongestChallengeData;

    private EditText Nickname;

    private Button SignoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(),"Loading Profile...",Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_my_profile);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        final String UID = UserInfo.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("User").child(UID);
        if (currentUser==null){
            currentUser = new User();
        }

        LongestStreakData = (TextView)findViewById(R.id.StreakData);
        AveragePomoTimeData = (TextView)findViewById(R.id.pomotimeData);
        TimeChallengedData = (TextView)findViewById(R.id.timeChallData);
        ChallengeWonData = (TextView)findViewById(R.id.WonData);
        LongestChallengeData = (TextView)findViewById(R.id.LongestChallData);
        Nickname = (EditText)findViewById(R.id.editTextTextPersonName2);

        this.SignoutButton = (Button)findViewById(R.id.email_sign_out_button);

        //System.out.println(UID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.child("UID").getValue(String.class));
                //System.out.println(snapshot.child("Nickname").getValue(String.class));
                //System.out.println(snapshot.child("LongestChallenge").getValue(long.class));
                //System.out.println(snapshot.child("TimeChallenge").getValue(long.class));
                //System.out.println(snapshot.child("ChallengeWin").getValue(long.class));
                //System.out.println(snapshot.child("LongestChallenge").getValue(long.class));
                
                //updateData(snapshot);
                LongestStreakData.setText(snapshot.child("LongestChallenge").getValue(long.class).toString());
                AveragePomoTimeData.setText(snapshot.child("AveragePomoTime").getValue(long.class).toString());
                TimeChallengedData.setText(snapshot.child("TimeChallenge").getValue(long.class).toString());
                ChallengeWonData.setText(snapshot.child("ChallengeWin").getValue(long.class).toString());
                LongestChallengeData.setText(snapshot.child("LongestChallenge").getValue(long.class).toString());
                Nickname.setText(snapshot.child("Nickname").getValue(String.class));

                Toast.makeText(getApplicationContext(),"Successfully Loading Profile",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });

        this.SignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                if (mAuth.getCurrentUser()==null){
                    Toast.makeText(getApplicationContext(),"Successfully Logging Out",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MyProfile.this,LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Can not log out Now",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateData(DataSnapshot snapshot) {

    }


    @Override
    public void onStart() {
        super.onStart();

        System.out.println(currentUser.getAveragePomoTime());
        //System.out.println(currentUser.getNickname());
    }
}
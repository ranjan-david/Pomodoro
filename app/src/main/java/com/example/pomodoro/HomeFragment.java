package com.example.pomodoro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class HomeFragment extends Fragment {
    //Set up the firebase parameters
    public DatabaseReference myRef;

    //Set up the original data
    private TextView LongestStreakData;
    private TextView AveragePomoTimeData;
    private TextView TimeChallengedData;
    private TextView ChallengeWonData;
    private TextView LongestChallengeData;
    private TextView location;
    private EditText Nickname;
    private EditText ChallengeTime;

    //Set up the local button of signout and save
    private Button SignoutButton;
    private Button SaveButton;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //loading profile from the database
        Toast.makeText(getActivity().getApplicationContext(),"Loading Profile...",Toast.LENGTH_SHORT).show();
        //Initial the current user
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        //Get the user id of current use to find the data of this user
        final String UID = UserInfo.getUid();

        //Initial database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Try to find the data of current user
        myRef = database.getReference().child("User").child(UID);

        //Binding the variables with the textview in front end
        LongestStreakData = (TextView)view.findViewById(R.id.StreakData);
        AveragePomoTimeData = (TextView)view.findViewById(R.id.pomotimeData);
        TimeChallengedData = (TextView)view.findViewById(R.id.timeChallData);
        ChallengeWonData = (TextView)view.findViewById(R.id.WonData);//
        LongestChallengeData = (TextView)view.findViewById(R.id.LongestChallData);
        Nickname = (EditText)view.findViewById(R.id.editTextTextPersonName2);
        location = (TextView) view.findViewById(R.id.textView_location);
        ChallengeTime = (EditText)view.findViewById(R.id.editTextChallengeTime);

        //Binding the buttons
        this.SignoutButton = (Button)view.findViewById(R.id.email_sign_out_button);
        this.SaveButton = (Button)view.findViewById(R.id.name_save);


        //Read data from backend
        //Using the framework of Firebase database
        //More information could be found in https://firebase.google.com/docs/database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Try to read the data
                try {
                    LongestStreakData.setText(snapshot.child("LongestChallenge").getValue(long.class).toString());
                    AveragePomoTimeData.setText(snapshot.child("AveragePomoTime").getValue(long.class).toString());
                    TimeChallengedData.setText(snapshot.child("TimeChallenge").getValue(long.class).toString());
                    ChallengeWonData.setText(snapshot.child("ChallengeWin").getValue(long.class).toString());
                    LongestChallengeData.setText(snapshot.child("LongestChallenge").getValue(long.class).toString());
                    ChallengeTime.setText(snapshot.child("Challengetime").getValue(long.class).toString());
                    Nickname.setText(snapshot.child("Nickname").getValue(String.class));
                    location.setText(snapshot.child("LocationName").getValue(String.class));
                    Toast.makeText(getActivity().getApplicationContext(),"Successfully Loading Profile",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    throw e;
                }
                //Read the Value from database


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Errors! Please check your connection");
            }
        });

        //Sign Out functionality
        this.SignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get user id of current user
                String UID = mAuth.getCurrentUser().getUid();
                //sign out using firebase author
                mAuth.signOut();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                //If logout successfully
                if (mAuth.getCurrentUser()==null){
                    Toast.makeText(getActivity().getApplicationContext(),"Successfully Logging Out",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity().getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    //Set the status to offline
                    database.child("User").child(UID).child("LoginState").setValue("Offline");
                }
                //if Logout failed
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Can not log out Now",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Save new Nick name
        this.SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Nickname").setValue(Nickname.getText().toString());
                String timechallenge = ChallengeTime.getText().toString();
                long num=10;
                try {
                    num = Integer.parseInt(timechallenge);
                    if (num>60){
                        Toast.makeText(getActivity().getApplicationContext(),"Challenge Time should be smaller than 60 mins",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        myRef.child("Challengetime").setValue(num);
                    }
                }catch(NumberFormatException nfe) {
                    Toast.makeText(getActivity().getApplicationContext(),"Wrong Format of Challenge Time!",Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getActivity().getApplicationContext(),"Successfully Save Your Profile！",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}

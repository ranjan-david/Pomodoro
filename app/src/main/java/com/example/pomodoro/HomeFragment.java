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
    public User currentUser;
    public DatabaseReference myRef;

    private TextView LongestStreakData;
    private TextView AveragePomoTimeData;
    private TextView TimeChallengedData;
    private TextView ChallengeWonData;
    private TextView LongestChallengeData;
    private TextView location;
    private EditText Nickname;

    private Button SignoutButton;
    private Button SaveButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        Toast.makeText(getActivity().getApplicationContext(),"Loading Profile...",Toast.LENGTH_SHORT).show();

        //setContentView(R.layout.activity_my_profile);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        final String UID = UserInfo.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("User").child(UID);
        if (currentUser==null){
            currentUser = new User();
        }

        LongestStreakData = (TextView)view.findViewById(R.id.StreakData);
        AveragePomoTimeData = (TextView)view.findViewById(R.id.pomotimeData);
        TimeChallengedData = (TextView)view.findViewById(R.id.timeChallData);
        ChallengeWonData = (TextView)view.findViewById(R.id.WonData);
        LongestChallengeData = (TextView)view.findViewById(R.id.LongestChallData);
        Nickname = (EditText)view.findViewById(R.id.editTextTextPersonName2);
        location = (TextView) view.findViewById(R.id.textView_location);

        this.SignoutButton = (Button)view.findViewById(R.id.email_sign_out_button);
        this.SaveButton = (Button)view.findViewById(R.id.name_save);

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
                location.setText(snapshot.child("Location").getValue(String.class));

                Toast.makeText(getActivity().getApplicationContext(),"Successfully Loading Profile",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getActivity().getApplicationContext(),"Successfully Logging Out",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity().getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Can not log out Now",Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Nickname").setValue(Nickname.getText().toString());
                Toast.makeText(getActivity().getApplicationContext(),"Successfully Save Your Nickname！",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}

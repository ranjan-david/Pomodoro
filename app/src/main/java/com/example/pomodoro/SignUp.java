package com.example.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    //Initial the button
    private Button BacktoSign;
    private Button SignUp;

    //Initial the Edit test
    private EditText MailCurrent;
    private EditText Password1;
    private EditText Password2;

    //Initial Firebase authority
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Binding the button
        this.BacktoSign = (Button)findViewById(R.id.back);
        this.SignUp = (Button)findViewById(R.id.signup);

        //Binding the user input
        this.MailCurrent = (EditText)findViewById(R.id.emailSignUp);
        this.Password1 = (EditText)findViewById(R.id.password1);
        this.Password2 = (EditText)findViewById(R.id.password2);

        //Binding the FirebaseAuth instance
        this.mAuth = FirebaseAuth.getInstance();

        //Click on back to sign
        //go back to sign in pages
        this.BacktoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //Click on sign up button
        this.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check whether current email is empty
                if (MailCurrent == null) {
                    Toast.makeText(getApplicationContext(), "Please enter Your email !", Toast.LENGTH_SHORT).show();
                } else {
                    //check whether two passwords are the same
                    if (!Password1.getText().toString().equals(Password2.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Different Password !", Toast.LENGTH_SHORT).show();
                    } else {
                        //Check the length of password
                        String emailInput = MailCurrent.getText().toString();
                        String passwordInput = Password1.getText().toString();
                        if (passwordInput.length() <= 5) {
                            Toast.makeText(SignUp.this, "Password should not be empty or less than 6 characters", Toast.LENGTH_SHORT).show();
                        } else {
                            //Create new user
                            Toast.makeText(SignUp.this, "Creating... ", Toast.LENGTH_SHORT).show();

                            //Call function in Firebase
                            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //Sign in successfully
                                                Toast.makeText(SignUp.this, "Successfully Create User! Generating Data...", Toast.LENGTH_SHORT).show();
                                                createData(mAuth.getCurrentUser());
                                                //Sign out first
                                                mAuth.signOut();
                                            } else {
                                                Toast.makeText(SignUp.this, "Create User failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });

    }
    private void createData(FirebaseUser currentUser){
        //Initial setting of the config values
        //All the integers are set to 0

        String UID = currentUser.getUid();
        String Nickname = "Pomodoro";
        String Location = "Not Check In";
        String Loginstate = "Offline";
        long LongestStreakData =0;
        long AveragePomoTimeData=0;
        long TimeChallengedData =0;
        long ChallengeWonData = 0;
        long LongestChallengeData = 0;
        long Latitude = 0;
        long Longitude = 0;

        //Initial all the parameters which will be used.
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("User").child(UID).setValue(1);
        database.child("User").child(UID).child("LongestChallenge").setValue(LongestStreakData);
        database.child("User").child(UID).child("AveragePomoTime").setValue(AveragePomoTimeData);
        database.child("User").child(UID).child("TimeChallenge").setValue(TimeChallengedData);
        database.child("User").child(UID).child("ChallengeWin").setValue(ChallengeWonData);
        database.child("User").child(UID).child("LongestChallenge").setValue(LongestChallengeData);
        database.child("User").child(UID).child("Nickname").setValue(Nickname);
        database.child("User").child(UID).child("Location").setValue(Location);
        database.child("User").child(UID).child("LoginState").setValue(Loginstate);
        database.child("User").child(UID).child("Latitude").setValue(Latitude);
        database.child("User").child(UID).child("Longitude").setValue(Longitude);
        database.child("User").child(UID).child("LatestLocation").setValue("0");



        Toast.makeText(SignUp.this, "Sign up Successfully, Back to Login...", Toast.LENGTH_SHORT).show();

        //Return to login page if sign up is done
        Intent intent=new Intent(SignUp.this,LoginActivity.class);
        startActivity(intent);
    }
}
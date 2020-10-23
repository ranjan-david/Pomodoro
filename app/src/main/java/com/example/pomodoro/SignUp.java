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
    private Button BacktoSign;
    private Button SignUp;

    private EditText MailCurrent;
    private EditText Password1;
    private EditText Password2;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.BacktoSign = (Button)findViewById(R.id.back);
        this.SignUp = (Button)findViewById(R.id.signup);

        this.MailCurrent = (EditText)findViewById(R.id.emailSignUp);
        this.Password1 = (EditText)findViewById(R.id.password1);
        this.Password2 = (EditText)findViewById(R.id.password2);

        this.mAuth = FirebaseAuth.getInstance();

        this.BacktoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        this.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MailCurrent == null) {
                    Toast.makeText(getApplicationContext(), "Please enter Your email !", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Password1.getText().toString().equals(Password2.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Different Password !", Toast.LENGTH_SHORT).show();
                    } else {
                        String emailInput = MailCurrent.getText().toString();
                        String passwordInput = Password1.getText().toString();
                        if (passwordInput.length() <= 5) {
                            Toast.makeText(SignUp.this, "Password should not be empty or less than 6 characters", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, "Creating... It might take a long time", Toast.LENGTH_SHORT).show();
                            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                //FirebaseUser user = mAuth.getCurrentUser();
                                                Toast.makeText(SignUp.this, "Successfully Create User! Generating Data...",
                                                        Toast.LENGTH_SHORT).show();
                                                createData(mAuth.getCurrentUser());
                                                mAuth.signOut();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(SignUp.this, "Create User failed.",
                                                        Toast.LENGTH_SHORT).show();
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
        String UID = currentUser.getUid();
        String Nickname = "Pomodoro";
        String Location = "Not Check In";
        String Loginstate = "Offline";
        long LongestStreakData =0;
        long AveragePomoTimeData=0;
        long TimeChallengedData =0;
        long ChallengeWonData = 0;
        long LongestChallengeData = 0;

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



        Toast.makeText(SignUp.this, " Generating Data Successfully",
                Toast.LENGTH_SHORT).show();
    }
}
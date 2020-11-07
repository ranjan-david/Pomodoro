package com.example.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    //Initialize the firebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Initialize Buttons
    private Button SigninButton;
    private Button SignupButton;
    private Button ResetPassword;

    //Initialize EditText
    private EditText MailCurrent;
    private EditText PasswordCurrent;

    //Init button UI
    private AnimationDrawable animationbtn1;
    private AnimationDrawable animationbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Get the instance of FirebaseAuth
        this.mAuth = FirebaseAuth.getInstance();

        //Binding the buttons
        this.SigninButton = (Button)findViewById(R.id.email_sign_in_button);
        //this.SignupButton = (Button)findViewById(R.id.email_sign_up_button);
        //this.ResetPassword = (Button)findViewById(R.id.resetPassword);
        //Binding the edit text
        this.MailCurrent = (EditText)findViewById(R.id.email);
        this.PasswordCurrent = (EditText)findViewById(R.id.password);

        //Set the Button UI
        //Reference & Idea from Android Studio - Custom Button - Dynamic Background
        //By Desarrollador Creativo
        //from https://www.youtube.com/watch?v=JUgoVCdF5kY
        animationbtn1 = (AnimationDrawable)SigninButton.getBackground();
        animationbtn1.setEnterFadeDuration(3000);
        animationbtn1.setExitFadeDuration(3000);

        TextView title = (TextView)findViewById(R.id.textView);
        animationbg = (AnimationDrawable)title.getBackground();
        animationbg.setEnterFadeDuration(4500);
        animationbg.setExitFadeDuration(4500);

        //Check whether there exists user already login
        //Using the framework of FirebaseAuth
        //More information in https://firebase.google.com/docs/auth

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                //Try to Get current User
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),"Currently Login with "+user.getEmail(),Toast.LENGTH_SHORT).show();
                    updateUI(mAuth.getCurrentUser());
                    moveToProfile(mAuth.getCurrentUser());
                } else {
                    Toast.makeText(getApplicationContext(),"Please Login Again.",Toast.LENGTH_SHORT).show();
                }
            }
        };

        //Sign in Button functionality
        //Using the framework of FirebaseAuth
        //More information in https://firebase.google.com/docs/auth
        this.SigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Read the email and password
                String emailInput = MailCurrent.getText().toString();
                String passwordInput = PasswordCurrent.getText().toString();

                if (!emailInput.isEmpty()&&passwordInput.length()>5){
                    Toast.makeText(LoginActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Get the user after Signing
                                FirebaseUser user = mAuth.getCurrentUser();
                                //Get the database of current user
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                                //Set the Values when Sign in
                                database.child("User").child(user.getUid()).child("LoginState").setValue("Online");
                                database.child("User").child(user.getUid()).child("LatestLocation").setValue("0");

                                //Go to HomePage
                                moveToProfile(mAuth.getCurrentUser());
                            } else {
                                //If Auth Failed, show the information
                                String error= task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, "Authentication failed." +error, Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }

                        }
                    });

                }
                else{
                    //If password is not following structure
                    Toast.makeText(getApplicationContext(),"Password should not be empty or less than 6 characters",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //Sign up
    public void onClickSignup(View v){
        Intent intent=new Intent(LoginActivity.this,SignUp.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //Reset Password
    public void onClickReset(View v){
        Intent intent=new Intent(LoginActivity.this,ResetPassword.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //Move to home page
    private void moveToProfile(FirebaseUser currentUser) {
        if (currentUser!=null){
            Intent intent=new Intent(LoginActivity.this,Pomodoro.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void onStart() {
        super.onStart();

        //Check whether user is login
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(getApplicationContext(),"Currently Login with "+currentUser.getEmail(),Toast.LENGTH_SHORT).show();

            //Update status of user
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("User").child(currentUser.getUid()).child("LoginState").setValue("Online");
            database.child("User").child(currentUser.getUid()).child("LatestLocation").setValue("0");

            //Move to Homepage
            moveToProfile(mAuth.getCurrentUser());
        }
        else{
            System.out.println("Please Login !");
        }
    }

    //Reference & Idea from Android Studio - Custom Button - Dynamic Background
    //By Desarrollador Creativo
    //from https://www.youtube.com/watch?v=JUgoVCdF5kY
    @Override
    protected void onResume(){
        super.onResume();

        if (animationbtn1!=null){
            if (!animationbtn1.isRunning()){
                animationbtn1.start();
            }
        }

        if (animationbg!=null){
            if (!animationbg.isRunning()){
                animationbg.start();
            }
        }

    }

    //Reference & Idea from Android Studio - Custom Button - Dynamic Background
    //By Desarrollador Creativo
    //from https://www.youtube.com/watch?v=JUgoVCdF5kY
    @Override
    protected void onPause(){
        super.onPause();
        if (animationbtn1!=null){
            if (animationbtn1.isRunning()){
                animationbtn1.stop();
            }
        }
        if (animationbg!=null){
            if (animationbg.isRunning()){
                animationbg.stop();
            }
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "Login Failed!" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Login in as " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    //Initialize the FirebaseAuth
    private FirebaseAuth mAuth;

    //Initialize the Button
    private Button BacktoSign;
    private Button ResetPassword;

    //Initialize the Edit Text
    private EditText MailCurrent;

    //Init button UI
    private AnimationDrawable animationbtn1;
    private AnimationDrawable animationbtn2;
    private AnimationDrawable animationbtn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Binding the FirebaseAuth
        this.mAuth = FirebaseAuth.getInstance();

        //Binding the Buttons
        this.BacktoSign = (Button)findViewById(R.id.back);
        this.ResetPassword = (Button)findViewById(R.id.send_email);

        //Binding the Edit text
        this.MailCurrent = (EditText)findViewById(R.id.email);

        //Set the Button UI
        //Reference & Idea from Android Studio - Custom Button - Dynamic Background
        //By Desarrollador Creativo
        //from https://www.youtube.com/watch?v=JUgoVCdF5kY
        animationbtn1 = (AnimationDrawable)BacktoSign.getBackground();
        animationbtn1.setEnterFadeDuration(3000);
        animationbtn1.setExitFadeDuration(3000);

        animationbtn2 = (AnimationDrawable)ResetPassword.getBackground();
        animationbtn2.setEnterFadeDuration(3000);
        animationbtn2.setExitFadeDuration(3000);

        TextView title = (TextView)findViewById(R.id.textView_resetPassword) ;
        animationbtn3 = (AnimationDrawable)title.getBackground();
        animationbtn3.setEnterFadeDuration(3000);
        animationbtn3.setExitFadeDuration(3000);
        //Link to Sign In page
        this.BacktoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResetPassword.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //Send the Email with reset password link
        //Using the framework of FireBase
        //More information in https://firebase.google.com/docs/auth

        this.ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the current Email
               String currentMail = MailCurrent.getText().toString();
               if (currentMail!=null){
                   Toast.makeText(getApplicationContext(),"Email Sent Successfully",Toast.LENGTH_SHORT).show();
                   mAuth.sendPasswordResetEmail(currentMail)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       //Toast.makeText(getApplicationContext(),"Email Sent Successfully",Toast.LENGTH_SHORT).show();
                                   }
                                   else {
                                       //Error Occurs
                                       Toast.makeText(getApplicationContext(),"Email Sent Failed, Check Your Email is correct",Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }
               else{
                   //Empty Email
                   Toast.makeText(getApplicationContext(),"Please Enter Your Email",Toast.LENGTH_SHORT).show();
               }
            }
        });

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
        if (animationbtn2!=null){
            if (!animationbtn2.isRunning()){
                animationbtn2.start();
            }
        }
        if (animationbtn3!=null){
            if (!animationbtn3.isRunning()){
                animationbtn3.start();
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
        if (animationbtn2!=null){
            if (animationbtn2.isRunning()){
                animationbtn2.stop();
            }
        }
        if (animationbtn3!=null){
            if (animationbtn3.isRunning()){
                animationbtn3.stop();
            }
        }
    }
}
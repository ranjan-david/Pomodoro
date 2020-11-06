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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    //Initialize the FirebaseAuth
    private FirebaseAuth mAuth;

    //Initialize the Button
    private Button BacktoSign;
    private Button ResetPassword;

    //Initialize the Edit Text
    private EditText MailCurrent;

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

        //Link to Sign In page
        this.BacktoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResetPassword.this,LoginActivity.class);
                startActivity(intent);
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
}
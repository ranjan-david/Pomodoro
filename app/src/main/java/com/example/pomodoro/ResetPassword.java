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
    private FirebaseAuth mAuth;
    private Button BacktoSign;
    private Button ResetPassword;

    private EditText MailCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.mAuth = FirebaseAuth.getInstance();
        this.BacktoSign = (Button)findViewById(R.id.back);
        this.ResetPassword = (Button)findViewById(R.id.send_email);

        this.MailCurrent = (EditText)findViewById(R.id.email);

        this.BacktoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResetPassword.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        this.ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                   else{
                                       //Toast.makeText(getApplicationContext(),"Email Sent Failed, Please check your email entered",Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }
               else{
                   Toast.makeText(getApplicationContext(),"Please Enter Your Email",Toast.LENGTH_SHORT).show();
               }
            }
        });

    }
}
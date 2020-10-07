package com.example.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button SigninButton;
    private Button SignoutButton;
    private Button SignupButton;

    private EditText MailCurrent;
    private EditText PasswordCurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mAuth = FirebaseAuth.getInstance();

        this.SigninButton = (Button)findViewById(R.id.email_sign_in_button);
        this.SignoutButton = (Button)findViewById(R.id.email_sign_out_button);
        this.SignupButton = (Button)findViewById(R.id.email_sign_up_button);
        this.MailCurrent = (EditText)findViewById(R.id.email);
        this.PasswordCurrent = (EditText)findViewById(R.id.password);

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),"Currently Login with "+user.getEmail(),Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(),"Please Login Again.",Toast.LENGTH_SHORT).show();

                }
            }
        };
        this.SigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = MailCurrent.getText().toString();
                String passwordInput = PasswordCurrent.getText().toString();
                //System.out.println(emailInput+" "+passwordInput);
                if (!emailInput.isEmpty()&&passwordInput.length()>5){
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                            ;
                        }
                    });
                    updateUI(mAuth.getCurrentUser());
                }
                else{
                    Toast.makeText(getApplicationContext(),"Password should not be empty or less than 6 characters",Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.SignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"Successfully Sign out",Toast.LENGTH_SHORT).show();

            }
        });

        this.SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = MailCurrent.getText().toString();
                String passwordInput = PasswordCurrent.getText().toString();
                mAuth.createUserWithEmailAndPassword(emailInput,passwordInput)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
                //updateUI(mAuth.getCurrentUser());
            }
        });

    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "Login Failed!" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Login in as" + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

}
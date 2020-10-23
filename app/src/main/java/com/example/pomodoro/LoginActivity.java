package com.example.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
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

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button SigninButton;

    private Button SignupButton;
    private Button ResetPassword;

    private EditText MailCurrent;
    private EditText PasswordCurrent;


    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mAuth = FirebaseAuth.getInstance();


        this.SigninButton = (Button)findViewById(R.id.email_sign_in_button);

        this.SignupButton = (Button)findViewById(R.id.email_sign_up_button);
        this.ResetPassword = (Button)findViewById(R.id.resetPassword);
        this.MailCurrent = (EditText)findViewById(R.id.email);
        this.PasswordCurrent = (EditText)findViewById(R.id.password);

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
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
        this.SigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = MailCurrent.getText().toString();
                String passwordInput = PasswordCurrent.getText().toString();
                //System.out.println(emailInput+" "+passwordInput);
                Toast.makeText(LoginActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                if (!emailInput.isEmpty()&&passwordInput.length()>5){
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                updateUI(mAuth.getCurrentUser());
                                moveToProfile(mAuth.getCurrentUser());
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                database.child("User").child(user.getUid()).child("LoginState").setValue("Online");
                                System.out.println("heheheheheeheheheheh");
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(),"Password should not be empty or less than 6 characters",Toast.LENGTH_SHORT).show();
                }
            }
        });
        

        this.SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

        this.ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ResetPassword.class);
                startActivity(intent);
            }
        });

    }

    private void moveToProfile(FirebaseUser currentUser) {
        if (currentUser!=null){
            Intent intent=new Intent(LoginActivity.this,Pomodoro.class);
            startActivity(intent);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void signIn() {
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getApplicationContext(),"Currently Login with "+currentUser.getEmail(),Toast.LENGTH_SHORT).show();
            updateUI(mAuth.getCurrentUser());
            moveToProfile(mAuth.getCurrentUser());
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("User").child(currentUser.getUid()).child("LoginState").setValue("Online");
            System.out.println("heheheheheeheheheheh");
        }
        else{
            System.out.println("No user!");
        }
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
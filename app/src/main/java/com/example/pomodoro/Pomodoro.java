package com.example.pomodoro;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Pomodoro extends AppCompatActivity {

    private MotionControlService motionControlService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    public void onDestroy() {

        super.onDestroy();
        /*
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String uid = UserInfo.getUid();
        if (UserInfo!=null){
            System.out.print("UserInfo:");

            System.out.println(UserInfo);

            database.child("User").child(uid).child("LoginState").setValue("Offline");
        }
        */
    }



    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MotionControlService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MotionControlService.MotionControlBinder binder = (MotionControlService.MotionControlBinder) service;
            motionControlService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.menuHome:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.menuPeople:
                            selectedFragment = new PeopleFragment();
                            break;
                        case R.id.menuPlaces:
                            selectedFragment = new PlacesFragment();
                            break;
                        case R.id.menuTimer:
                            selectedFragment = new PomodoroFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };
}
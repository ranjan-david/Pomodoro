package com.example.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//An activity which contains the bottom nav bar and space for feature fragments
public class Pomodoro extends AppCompatActivity {

    public void callMain(View view){
        Log.d("MyApp","I am here1");
        Intent monintent = new Intent(Pomodoro.this, maincaller.class);
        startActivity(monintent);

    }
    public void callPdf(View view){
        Log.d("MyApp","I am here22");
        Intent newIntent = new Intent(Pomodoro.this, pdfcaller.class);
        startActivity(newIntent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pomodoro);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        //initial fragment is the home fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        // set the navlistener for the navbar
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


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                //Load fragment based on which button is pressed
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
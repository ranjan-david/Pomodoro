package com.example.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.pomodoro.Base.BaseActivity;

public class maincaller extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_search);
        Intent inintent = new Intent(maincaller.this, myActivity.class);
        startActivity(inintent);

        //myac.initFrescoLibrary();
        //myac.initViews();
        //myac.loadDefaultCard();
        Log.d("MyApp","I am here10");

    }
}

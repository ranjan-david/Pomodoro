package com.example.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.pomodoro.Base.BaseActivity;

public class pdfcaller extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_search);
        Intent inintent = new Intent(pdfcaller.this, MainActivity.class);
        startActivity(inintent);

        Log.d("MyApp","I am here10");

    }
}

package com.example.pomodoro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyActivity extends AppCompatActivity {
    String s1[], s2[];
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.RecyclerView);

        s1 = getResources().getStringArray(R.array.example_item); // Get the string eg. name of nearby place
        s2 = getResources().getStringArray(R.array.example_description);

        MyAdapter newAdapter = new MyAdapter(this, s1, s2); // Pass strings to layout

        recyclerView.setAdapter(newAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
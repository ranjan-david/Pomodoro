package com.example.challenge_page;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView countdowntext;
    private Button countdown_btn;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 600000 ;
    private boolean timeRunning;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countdowntext = findViewById(R.id.timer);
        countdown_btn = findViewById(R.id.btn_start);

        countdown_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStop();
            }
        });
        updateTimer();

    }
    public void startStop(){
        if (timeRunning){
            stopTimer();
        } else{
            startTimer();
        }
    }
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        countdown_btn.setText("PAUSE");
        timeRunning = true;
    }
    public void stopTimer(){
        countDownTimer.cancel();
        countdown_btn.setText("START");
        timeRunning = false;
    }
    public void updateTimer(){
        int minutes = (int) timeLeftInMilliseconds / 60000;
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;
        String timeleftText;

        timeleftText = "" + minutes;
        timeleftText += ":";
        if (seconds < 10) timeleftText += "0";
        timeleftText += seconds;

        countdowntext.setText(timeleftText);
    }
}
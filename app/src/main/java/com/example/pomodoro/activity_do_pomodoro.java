package com.example.pomodoro;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class activity_do_pomodoro extends AppCompatActivity {
    Integer studyTime, shortBreakTime, longBreakTime, repeatsTillLongBreak;
    TextView timerTextView;
    TextView timerLabelTextView;
    Integer breakCount = 0;
    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_pomodoro);

        Intent intent = getIntent();
        studyTime = intent.getIntExtra("studyTime", 0);
        shortBreakTime = intent.getIntExtra("shortBreakTime", 0);
        longBreakTime = intent.getIntExtra("longBreakTime", 0);
        repeatsTillLongBreak = intent.getIntExtra("repeatsTillLongBreak", 0);
        timerTextView = (TextView)findViewById(R.id.sampleTextView);
        timerLabelTextView = (TextView)findViewById(R.id.timerLabelTextView);

        timerTextView.setText(studyTime.toString() + ' ' + shortBreakTime.toString());
        timerLabelTextView.setText("Go Study!");

        startTimer();


        Button button = (Button) findViewById(R.id.cancelButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_do_pomodoro.this, Pomodoro.class);
                startActivity(myIntent);
            }
        });
    }

    private void startTimer(){
        new CountDownTimer(studyTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                playNotification();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopNotification();
                if(breakCount % repeatsTillLongBreak == 0)
                {
                    timerLabelTextView.setText("Enjoy your long break. Stretch your legs!");
                    startLongBreak();
                }
                else
                {
                    timerLabelTextView.setText("Enjoy your short break");
                    startShortBreak();
                }
            }
        }.start();
    }

    private void startShortBreak(){
        new CountDownTimer(shortBreakTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                breakCount++;
                playNotification();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopNotification();
                timerLabelTextView.setText("Go Study!");
                startTimer();
            }
        }.start();
    }

    private void startLongBreak(){
        new CountDownTimer(longBreakTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                breakCount++;
                playNotification();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopNotification();
                timerLabelTextView.setText("Go Study!");
                startTimer();
            }
        }.start();
    }

    private void playNotification(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopNotification(){
        try {
            r.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

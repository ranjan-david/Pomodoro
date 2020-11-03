package com.example.pomodoro;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class DoPomodoroActivity extends AppCompatActivity {
    Integer studyTime, shortBreakTime, longBreakTime, repeatsTillLongBreak;
    TextView timerTextView;
    TextView timerLabelTextView;
    Integer breakCount = 0;
    Ringtone r;
    public DatabaseReference myDatabaseRef;
    public User currentUser;
    private MotionControlService motionControlService;
    boolean mBound = false;
    enum State { WAITING_FOR_START, SCREEN_DOWN, SCREEN_UP }
    State currentState = State.WAITING_FOR_START;
    CountDownTimer timer = null;
    long longestStreakData;
    long averagePomoTimeData;
    long timeChallengedData;
    long challengeWonData;
    long longestChallengeData;
    long numberOfPomos;
    long currentStreak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_pomodoro);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        final String UID = UserInfo.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myDatabaseRef = database.getReference().child("User").child(UID);
        if (currentUser==null){
            currentUser = new User();
        }

        Intent intent = getIntent();
        studyTime = intent.getIntExtra("studyTime", 0);
        shortBreakTime = intent.getIntExtra("shortBreakTime", 0);
        longBreakTime = intent.getIntExtra("longBreakTime", 0);
        repeatsTillLongBreak = intent.getIntExtra("repeatsTillLongBreak", 0);
        timerTextView = (TextView)findViewById(R.id.timerTextView);
        timerLabelTextView = (TextView)findViewById(R.id.timerLabelTextView);

        timerTextView.setText(studyTime.toString() + ' ' + shortBreakTime.toString());
        timerLabelTextView.setText("Go Study!");

        Button button = (Button) findViewById(R.id.cancelButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(DoPomodoroActivity.this, Pomodoro.class);
                startActivity(myIntent);
            }
        });


        try {
            checkSystemWritePermission();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Not giving permission will result in significantly lower battery life", Toast.LENGTH_LONG).show();
        }

        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                averagePomoTimeData = snapshot.child("AveragePomoTime").getValue(long.class);
                timeChallengedData = snapshot.child("TimeChallenge").getValue(long.class);
                challengeWonData = snapshot.child("ChallengeWin").getValue(long.class);
                longestChallengeData = snapshot.child("LongestChallenge").getValue(long.class);
                try{
                    numberOfPomos = snapshot.child("NumberOfPomos").getValue(long.class);
                }
                catch(Exception ex)
                {
                    Log.i("DoPomodoroActivity", "EXCEPTION");
                    numberOfPomos = 0;
                    myDatabaseRef.child("NumberOfPomos").setValue(numberOfPomos);
                }
                try{
                    longestStreakData = snapshot.child("LongestStreak").getValue(long.class);
                }
                catch(Exception ex) {
                    Log.i("DoPomodoroActivity", "EXCEPTION");
                    longestStreakData = 0;
                    myDatabaseRef.child("LongestStreak").setValue(longestStreakData);
                }
                //if(numberOfPomos != null)
                //{
                //    numberOfPomos = snapshot.child("NumberOfPomos").getValue(long.class);
               // }
              //  else
            //    {
             //   }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MotionControlService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        IntentFilter broadcastIntentFilter = new IntentFilter();
        broadcastIntentFilter.addAction(MotionControlService.BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), broadcastIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unbindService(connection);
        mBound = false;
        timer.cancel();

        if(currentStreak/60 >= 1)
        {
            if(numberOfPomos == 0)
            {
                averagePomoTimeData = currentStreak/60;
            }
            else
            {
                averagePomoTimeData = averagePomoTimeData + (currentStreak/60 - averagePomoTimeData)/numberOfPomos;
            }
            myDatabaseRef.child("AveragePomoTime").setValue(averagePomoTimeData);
            myDatabaseRef.child("NumberOfPomos").setValue(++numberOfPomos);
            if(currentStreak/60 > longestStreakData)
            {
                myDatabaseRef.child("LongestStreak").setValue(currentStreak/60);
            }
        }
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


    private void startTimer(){
        timer = new CountDownTimer(studyTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Seconds Remaining: " + millisUntilFinished / 1000);
                currentStreak += 1;
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
        timer = new CountDownTimer(shortBreakTime * 60000, 1000) {

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
        timer = new CountDownTimer(longBreakTime * 60000, 1000) {

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

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivity(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }
    };

    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(MotionControlService.BROADCAST_INTENT)) {
            String value = intent.getStringExtra(MotionControlService.BROADCAST_VALUE);
            if(currentState == State.WAITING_FOR_START && value == MotionControlService.DOWN)
            {
                try {
                    if (checkSystemWritePermission()) {
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.screenBrightness = 0;
                        getWindow().setAttributes(params);
                        WindowManager.LayoutParams params2 = getWindow().getAttributes();
                        Log.i("DoPomodoroActivity", "SCREEN BRIGHTNESS " + params2.screenBrightness);
                    }else {
                        Toast.makeText(this, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                    }
                    FrameLayout parentLayout = (FrameLayout)findViewById(R.id.root_view);
                    parentLayout.removeView(findViewById(R.id.info_overlay));
                    startTimer();
                }
                catch (Exception ex)
                {
                    Log.i("DoPomodoroActivity", "Exception occured: " + ex.getMessage());
                }
            }
            Log.i("DoPomodoroActivity", "Value received: " + value);
        }
    }

}

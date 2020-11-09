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

public class DoPomodoroActivity extends AppCompatActivity {
    Integer studyTime, shortBreakTime, longBreakTime, repeatsTillLongBreak;
    TextView timerTextView;
    TextView timerLabelTextView;
    Integer breakCount = 1;
    Ringtone r;
    public DatabaseReference myDatabaseRef;
    public User currentUser;
    private MotionControlService motionControlService;
    boolean mBound = false;
    enum State { WAITING_FOR_START, SCREEN_DOWN, SCREEN_UP }
    State currentState = State.WAITING_FOR_START;
    enum PomoState { NOT_STARTED, STUDY, SHORT_BREAK, LONG_BREAK }
    PomoState currentPomoState = PomoState.NOT_STARTED;
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

        //get values from the intent
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

                // Check if the current pomodoro is more than one minute long, update
                // details in database if it is
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
                // cancel timer on exit
                timer.cancel();
                currentState = State.WAITING_FOR_START;
                // Go back to home activity
                Intent myIntent = new Intent(DoPomodoroActivity.this, Pomodoro.class);
                startActivity(myIntent);
            }
        });


        Button continueBtn = (Button) findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            // On clicking the continue, stop the notification that is playing,
            // hide the continue button and start the next step
            public void onClick(View v) {
                stopNotification();
                startNextStep();
                hideContinueButton();
            }
        });

        try {
            // Check if the app has permission to lower the brightness of the screen
            checkSystemWritePermission();
        }
        catch (Exception e)
        {
            // Warn the user about higher battery usage if permission is not given
            Toast.makeText(this, "Not giving permission will result in significantly lower battery life", Toast.LENGTH_LONG).show();
        }

        // Set the user values from database
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
        // Start the motion control service
        Intent intent = new Intent(this, MotionControlService.class);
        startService(intent);
        // Bind the service to this activity
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // register a local broadcast receiver to get the broadcast from the service when the sensor changes
        IntentFilter broadcastIntentFilter = new IntentFilter();
        broadcastIntentFilter.addAction(MotionControlService.BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), broadcastIntentFilter);
        hideContinueButton();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unbind the service so that it can be destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unbindService(connection);
        mBound = false;
    }

    // serviceconnection for the motion control service
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MotionControlService.MotionControlBinder binder = (MotionControlService.MotionControlBinder) service;
            motionControlService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    // This method is called when the study timer is to be called
    private void startTimer(){
        // Define timer methods and start timer
        timer = new CountDownTimer(studyTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update UI on tick
                timerTextView.setText("Seconds Remaining: " + millisUntilFinished / 1000);
                // The current streak of the user
                currentStreak += 1;
            }

            public void onFinish() {
                playNotification();
                showContinueButton();
                /*
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
                 */
            }
        }.start();
        // Set the current state to study
        currentPomoState = PomoState.STUDY;
    }

    private void startShortBreak(){
        // Define timer methods and start timer
        timer = new CountDownTimer(shortBreakTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update UI on tick
                timerTextView.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            // Increment break count, play notification and show the continue button
            public void onFinish() {
                breakCount++;
                playNotification();
                showContinueButton();
                /*
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopNotification();
                timerLabelTextView.setText("Go Study!");
                startTimer();
                */
            }
        }.start();
        currentPomoState = PomoState.SHORT_BREAK;
    }

    private void startLongBreak(){
        // Define timer methods and start timer
        timer = new CountDownTimer(longBreakTime * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update UI on tick
                timerTextView.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            // Increment break count, play notification and show the continue button
            public void onFinish() {
                breakCount++;
                playNotification();
                showContinueButton();
                /*
                //try {
                //    TimeUnit.SECONDS.sleep(5);
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
               // }
                //stopNotification();
                //timerLabelTextView.setText("Go Study!");
                //startTimer();

                 */
            }
        }.start();
        currentPomoState = PomoState.LONG_BREAK;
    }

    // Plays the notification (alarm) sound on the device
    private void playNotification(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stops the notification on the device
    private void stopNotification(){
        try {
            r.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Based on the current step, start the next step in the pomodoro process
    // Also updates the UI message
    private void startNextStep()
    {
        if(currentPomoState == PomoState.STUDY)
        {
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
        else if(currentPomoState == PomoState.SHORT_BREAK)
        {
            timerLabelTextView.setText("Go Study!");
            startTimer();
        }
        else if(currentPomoState == PomoState.LONG_BREAK)
        {
            timerLabelTextView.setText("Go Study!");
            startTimer();
        }
    }

    // Makes the continue button visible
    private void showContinueButton()
    {
        Button continueBtn = (Button) findViewById(R.id.continueBtn);
        continueBtn.setVisibility(View.VISIBLE);
    }

    // Makes the continue button invisible
    private void hideContinueButton()
    {
        Button continueBtn = (Button) findViewById(R.id.continueBtn);
        continueBtn.setVisibility(View.INVISIBLE);
    }

    // Check if the app has permission to change system settings, ask if not.
    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    // opens the android permission menu for the WRITE_SETTINGS permission
    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivity(intent);
        }
    }

    // Broadcast message for the motion control service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }
    };

    // Handle the signal received from the motion control service
    private void handleIntent(Intent intent) {
        // If this is the correct broadcast intent
        if (intent.getAction().equals(MotionControlService.BROADCAST_INTENT)) {
            // Get the broadcast value
            String value = intent.getStringExtra(MotionControlService.BROADCAST_VALUE);
            if(value == MotionControlService.DOWN)
            {
                try {
                    // If the app has write permissions, then reduce the screen brightness
                    if (checkSystemWritePermission()) {
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.screenBrightness = 0;
                        getWindow().setAttributes(params);
                        WindowManager.LayoutParams params2 = getWindow().getAttributes();
                        Log.i("DoPomodoroActivity", "SCREEN BRIGHTNESS " + params2.screenBrightness);
                    }else {
                        Toast.makeText(this, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                    }
                    // If waiting for start, start the pomodoro
                    if(currentState == State.WAITING_FOR_START) {
                        FrameLayout parentLayout = (FrameLayout) findViewById(R.id.root_view);
                        parentLayout.removeView(findViewById(R.id.info_overlay));
                        startTimer();
                        currentPomoState = PomoState.STUDY;
                    }
                    currentState = State.SCREEN_DOWN;
                }
                catch (Exception ex)
                {
                    Log.i("DoPomodoroActivity", "Exception occured: " + ex.getMessage());
                }
            }
            if(value == MotionControlService.UP)
            {
                try {
                    // If the app has write permissions, then increase the screen brightness
                    if (checkSystemWritePermission()) {
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.screenBrightness = -1;
                        getWindow().setAttributes(params);
                        WindowManager.LayoutParams params2 = getWindow().getAttributes();
                        Log.i("DoPomodoroActivity", "SCREEN BRIGHTNESS " + params2.screenBrightness);
                    }else {
                        Toast.makeText(this, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                    }
                    currentState = State.SCREEN_UP;
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

package com.example.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.pomodoro.PeopleAdapter;
import com.example.pomodoro.Base.BaseActivity;
import com.example.pomodoro.Base.PublicMethods;
import com.example.pomodoro.common.CameraSource;
import com.example.pomodoro.common.CameraSourcePreview;
import com.example.pomodoro.common.FrameMetadata;
import com.example.pomodoro.common.GraphicOverlay;
import com.example.pomodoro.interfaces.FrameReturn;
import com.example.pomodoro.visions.FaceDetectionProcessor;
import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.IOException;
/**
 *
 * This is the challenge function written by Ramana Jeyaprakash - 1099310.
 *
 * 1.Packages used by this function -Base, Common(CameraSource, GraphicOverlay, FrameMetadata).
 * 2.uses firebase MLkit to detect smile and left and right eye open Probabilities
 * 3.gets the user name and challenge time from PeopleAdapter activity and allows to enter(start) the challenge when the user smiles.
 * 4.can detect eye movement and prompt user to open both eyes if closed
 * 5.if user completes challenge , number of challenges won by user is updated in firebase database
 * */


@KeepName
public final class Challenge extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, FrameReturn {
    private static final String FACE_DETECTION = "Face Detection";
    private static final String TAG = "MLKitTAG";




    Bitmap originalImage = null;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

//    for timer ===========

    private TextView countdowntext;
    private Button countdown_btn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;
    private boolean timeRunning;

    // to update database
    public User currentUser;
    public DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);

        //for timer ============
        countdown_btn = findViewById(R.id.start_challenge);
        countdowntext = findViewById(R.id.timer);


        if (PublicMethods.allPermissionsGranted(this)) {
            createCameraSource();
        } else {
            PublicMethods.getRuntimePermissions(this);
        }


        countdown_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });
        //updateTimer();

        // database ====================================


        Intent intent = getIntent();
        String message = intent.getStringExtra("Name");
        String Challenge_time = intent.getStringExtra("Time");

        String[] words = Challenge_time.split(" ");
        Challenge_time = words[0];

//
        long T = 0;
            T = Long.parseLong(Challenge_time);
            timeLeftInMilliseconds = T * 60000;

        // setting value for the challenge timer


        TextView textView = findViewById(R.id.Challenger_Name);
        textView.setText(message);

        updateTimer();

    }
    //for timer =======================================
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

        // if timeLeftInMilliseconds is 1000 - increase the challenge completed by one.
        if((minutes == 0) && (seconds == 0)){

            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser UserInfo =  mAuth.getCurrentUser();
            final String UID = UserInfo.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference().child("User").child(UID);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    long ChallengeWonData = snapshot.child("ChallengeWin").getValue(long.class);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                    ChallengeWonData = ChallengeWonData + 1;

                    database.child("User").child(UID).child("ChallengeWin").setValue(ChallengeWonData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            Toast toast = Toast.makeText(getApplicationContext(),
                    "You have Successfully completed the challenge",
                    Toast.LENGTH_SHORT);
            toast.show();

        }

    }
    // ======================================= for timer


    private void createCameraSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        try {
            FaceDetectionProcessor processor = new FaceDetectionProcessor(getResources());
            processor.frameHandler = this;

            cameraSource.setMachineLearningFrameProcessor(processor);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + FACE_DETECTION, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PublicMethods.allPermissionsGranted(this)) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //calls with each frame includes by face
    @Override
    public void onFrame(Bitmap image, FirebaseVisionFace face, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        originalImage = image;
        if (face.getLeftEyeOpenProbability() < 0.4) {
            findViewById(R.id.rightEyeStatus).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rightEyeStatus).setVisibility(View.INVISIBLE);
        }
        if (face.getRightEyeOpenProbability() < 0.4) {
            findViewById(R.id.leftEyeStatus).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.leftEyeStatus).setVisibility(View.INVISIBLE);
        }

        if (face.getSmilingProbability() >= .6) {
            countdown_btn.setEnabled(true);
        }
        else {
            countdown_btn.setEnabled(false);
        }


    }

    public void sendMessage(View view) {
        Intent intent1 = new Intent(Challenge.this, Pomodoro.class);
        startActivity(intent1);
    }






}

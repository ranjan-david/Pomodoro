package com.example.face_v1.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.face_v1.Base.BaseActivity;
import com.example.face_v1.Base.Cons;
import com.example.face_v1.Base.PublicMethods;
import com.example.face_v1.R;
import com.example.face_v1.common.CameraSource;
import com.example.face_v1.common.CameraSourcePreview;
import com.example.face_v1.common.FrameMetadata;
import com.example.face_v1.common.GraphicOverlay;
import com.example.face_v1.interfaces.FaceDetectStatus;
import com.example.face_v1.interfaces.FrameReturn;
import com.example.face_v1.models.RectModel;
import com.example.face_v1.visions.FaceDetectionProcessor;
import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.IOException;

import static com.example.face_v1.Base.Cons.IMG_EXTRA_KEY;

@KeepName
public final class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, FrameReturn {
    private static final String FACE_DETECTION = "Face Detection";
    private static final String TAG = "MLKitTAG";

    Bitmap originalImage = null;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;


    private Button startChallenge;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = findViewById(R.id.firePreview);
        startChallenge = findViewById(R.id.start_challenge);

        graphicOverlay = findViewById(R.id.fireFaceOverlay);


        if (PublicMethods.allPermissionsGranted(this)) {
            createCameraSource();
        } else {
            PublicMethods.getRuntimePermissions(this);
        }


        startChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


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
            startChallenge.setEnabled(true);
        }
        else {
            startChallenge.setEnabled(false);
        }


    }



}
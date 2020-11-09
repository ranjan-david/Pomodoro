package com.example.pomodoro.interfaces;

import android.graphics.Bitmap;

import com.example.pomodoro.common.FrameMetadata;
import com.example.pomodoro.common.GraphicOverlay;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

public interface FrameReturn{
    void onFrame(
            Bitmap image ,
            FirebaseVisionFace face ,
            FrameMetadata frameMetadata,
            GraphicOverlay graphicOverlay
    );
}

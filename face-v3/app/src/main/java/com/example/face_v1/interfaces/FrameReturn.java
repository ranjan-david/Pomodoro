package com.example.face_v1.interfaces;

import android.graphics.Bitmap;

import com.example.face_v1.common.FrameMetadata;
import com.example.face_v1.common.GraphicOverlay;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

public interface FrameReturn{
    void onFrame(
            Bitmap image ,
            FirebaseVisionFace face ,
            FrameMetadata frameMetadata,
            GraphicOverlay graphicOverlay
    );
}
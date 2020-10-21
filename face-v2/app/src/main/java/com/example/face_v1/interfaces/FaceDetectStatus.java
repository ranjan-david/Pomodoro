package com.example.face_v1.interfaces;

import com.example.face_v1.models.RectModel;


public interface FaceDetectStatus {
    void onFaceLocated(RectModel rectModel);
    void onFaceNotLocated() ;
}

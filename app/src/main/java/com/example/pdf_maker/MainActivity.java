package com.example.pdf_maker;

import com.example.pdf_maker.camera.CameraActivity;
import com.example.pdf_maker.util.RuntimePermissions;
import com.pixelnetica.imagesdk.MetaImage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
package com.example.face_v1.main;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.face_v1.Base.BaseActivity;
import com.example.face_v1.Base.PublicMethods;
import com.example.face_v1.R;

import static com.example.face_v1.Base.Cons.IMG_EXTRA_KEY;
import static com.example.face_v1.Base.Cons.IMG_FILE;

public class PhotoViewerActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        if (getIntent().hasExtra(IMG_EXTRA_KEY)) {
            ImageView imageView = findViewById(R.id.image);
            String imagePath = getIntent().getStringExtra(IMG_EXTRA_KEY);
            imageView.setImageBitmap(PublicMethods.getBitmapByPath(imagePath, IMG_FILE));
        }
    }
}

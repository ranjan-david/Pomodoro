package com.example.pdf_maker.camera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.annotation.IntDef;
import android.util.Log;

import com.example.pdf_maker.AppLog;
import com.example.pdf_maker.SdkFactory;
import com.example.pdf_maker.AppSdkFactory;
import com.example.pdf_maker.util.SequentialThread;
import com.pixelnetica.imagesdk.AutoShotDetector;
import com.pixelnetica.imagesdk.Corners;
import com.pixelnetica.imagesdk.ImageProcessing;
import com.pixelnetica.imagesdk.ImageSdkLibrary;
import com.pixelnetica.imagesdk.MetaImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FindDocCornersThread {
}

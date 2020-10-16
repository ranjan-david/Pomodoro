package com.example.pdf_maker.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pdf_maker.AppLog;//
import com.example.pdf_maker.R;//
import com.example.pdf_maker.util.ParcelableHolder;
import com.example.pdf_maker.util.RuntimePermissions;
import com.example.pdf_maker.AppSdkFactory;
import com.example.pdf_maker.util.Utils;
import com.example.pdf_maker.widget.ImageCheckBox;
import com.example.pdf_maker.widget.console.ConsoleView;

//import static com.example.pdf_maker.R.string.permission_query_write_storage;

public class CameraActivity {
}

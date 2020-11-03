package com.example.pomodoro;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

// Class idea and code components taken from https://stackoverflow.com/questions/17774070/android-detect-when-the-phone-flips-around

public class MotionControlService extends Service implements SensorEventListener {

    private final IBinder binder = new MotionControlBinder();
    private static final String TAG = "MotionControlService";

    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 2;
    private boolean mStarted = false;
    private SensorManager mSensorManager;
    public static final String BROADCAST_INTENT = "broadcast_intent";
    public static final String BROADCAST_VALUE = "broadcast_value";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public class MotionControlBinder extends Binder {
        MotionControlService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MotionControlService.this;
        }
    }

    public MotionControlService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand, Started: " + mStarted);

        if (!mStarted) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);

            mStarted = true;
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            float gz = event.values[2];
            Log.d(TAG, "EVENT TRIGGERED");
            if (mGZ == 0) {
                mGZ = gz;
            } else {
                if ((mGZ * gz) < 0) {
                    mEventCountSinceGZChanged++;
                    if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {
                        mGZ = gz;
                        mEventCountSinceGZChanged = 0;
                        if (gz > 0) {
                            Log.d(TAG, "now screen is facing up.");
                            sendBroadcast(MotionControlService.UP);
                        } else if (gz < 0) {
                            Log.d(TAG, "now screen is facing down.");
                            sendBroadcast(MotionControlService.DOWN);
                        }
                    }
                } else {
                    if (mEventCountSinceGZChanged > 0) {
                        mGZ = gz;
                        mEventCountSinceGZChanged = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    private void sendBroadcast(String value) {

        Intent intent = new Intent(BROADCAST_INTENT);
        intent.putExtra(BROADCAST_VALUE, value);
        broadcastManager.sendBroadcast(intent);
    }
}

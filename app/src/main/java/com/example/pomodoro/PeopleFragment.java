package com.example.pomodoro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PeopleFragment extends Fragment {
    private LocationManager myLocationManager;
    private LocationManager mLocationManager;
    Location location;
    Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate( R.layout.activity_peoplenearby, null);
        mLocationManager = (LocationManager) mContext.getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (checkGPSSetting()){
            peoplenearby();
        }
        addlistener();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser UserInfo =  mAuth.getCurrentUser();

        final String UID = UserInfo.getUid();
        System.out.println(UID);
        System.out.println(UID);
        System.out.println(UID);
        System.out.println(UID);

        return view;
    }


    public void addlistener(){
        LocationListener locationListener=new LocationListener() {

            /**
             * 位置信息变化时触发
             */
            public void onLocationChanged(Location location) {
                Log.i(TAG, "时间："+location.getTime());
                Log.i(TAG, "经度："+location.getLongitude());
                Log.i(TAG, "纬度："+location.getLatitude());
                Log.i(TAG, "海拔："+location.getAltitude());
            }

            /**
             * GPS状态变化时触发
             */
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    //GPS状态为可见时
                    case LocationProvider.AVAILABLE:
                        Log.i(TAG, "当前GPS状态为可见状态");
                        break;
                    //GPS状态为服务区外时
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.i(TAG, "当前GPS状态为服务区外状态");
                        break;
                    //GPS状态为暂停服务时
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.i(TAG, "当前GPS状态为暂停服务状态");
                        break;
                }
            }

            /**
             * GPS开启时触发
             */
            public void onProviderEnabled(String provider) {
                Location location=getLastKnownLocation();
                updateView(location);
            }

            /**
             * GPS禁用时触发
             */
            public void onProviderDisabled(String provider) {
                Location location=getLastKnownLocation();
                updateView(null);
            }


        };
    }

    public void peoplenearby(){





        location = getLastKnownLocation();



        System.out.println(location.getLatitude());
        System.out.println(location.getLatitude());
        System.out.println(location.getLatitude());





        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
    }

    public boolean checkGPSSetting() {
        boolean gpsenable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsenable == false) {
            openGPS2();
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                return true;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String []{android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            else{
                return true;



            }


        }
        return false;
    }

    public void updateView(Location location){
        System.out.println(location.getLatitude());
        System.out.println(location.getLatitude());
        System.out.println(location.getLatitude());
        this.location = location;
    }

    public void openGPS2(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0);
    }

    private Location getLastKnownLocation() {


        List<String> providers = mLocationManager.getProviders(true);

        Location bestLocation = null;


        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)




                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }




}


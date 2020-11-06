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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class PeopleFragment extends Fragment {
    private LocationManager myLocationManager;
    private LocationManager mLocationManager;
    Location location;
    Context mContext;
    @Nullable
    private FirebaseAuth mAuth;

    DatabaseReference database;
    ArrayList<String> nearbyList;
    View view;
    RecyclerView nicknameView;
    PeopleAdapter peopleAdapter;

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate( R.layout.activity_peoplenearby, null);
        nearbyList = new ArrayList<String>();

        mLocationManager = (LocationManager) mContext.getApplicationContext().getSystemService(LOCATION_SERVICE);
        this.mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference logout = FirebaseDatabase.getInstance().getReference();



        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        logout.child("User").child(UserInfo.getUid()).child("LoginState").onDisconnect().setValue("Offline");

        final String UID = UserInfo.getUid();
        if (checkGPSSetting()){
            Location location = getLastKnownLocation();
            updateView(location);
            database.child("User").child(UID).child("LatestLocation").setValue("1");
            peoplenearby();
            nicknameView = view.findViewById(R.id.nearbylayout);
            peopleAdapter = new PeopleAdapter(getActivity(),nearbyList);
            nicknameView.setAdapter(peopleAdapter);

            nicknameView.setLayoutManager(new LinearLayoutManager(getActivity()));




        }
        addlistener();




        return view;
    }

    public void onDestroy() {

        super.onDestroy();
    }


    public void addlistener(){
        LocationListener locationListener=new LocationListener() {

            public void onLocationChanged(Location location) {
                Log.i(TAG, "time："+location.getTime());
                Log.i(TAG, "Longitude："+location.getLongitude());
                Log.i(TAG, "Latitude："+location.getLatitude());
                Log.i(TAG, "Altitud："+location.getAltitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        Log.i(TAG, "visible");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.i(TAG, "out of service");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.i(TAG, "TEMPORARILY_UNAVAILABLE");
                        break;
                }
            }
            public void onProviderEnabled(String provider) {
                Location location=getLastKnownLocation();
                updateView(location);
            }

            public void onProviderDisabled(String provider) {
                Location location=getLastKnownLocation();
                updateView(null);
            }


        };
    }

    public void createRecycleView(){
        //
        peopleAdapter.notifyDataSetChanged();



    }

    public void peoplenearby(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> a = dataSnapshot.child("User").getChildren();
                distanceCheck(dataSnapshot,a);
                createRecycleView();





                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        database.addValueEventListener(postListener);

    }

    public void distanceCheck(DataSnapshot dataSnapshot,Iterable<DataSnapshot> dataSnapshots){
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        String UID = UserInfo.getUid();
        nearbyList.clear();
        for (DataSnapshot d:dataSnapshots){
            if (boolConnected(d)&&!d.getKey().equals(UID)){

                double this_latitude = Double.parseDouble(dataSnapshot.child("User").child(UID).child("Latitude").getValue().toString());
                double this_longitude = Double.parseDouble(dataSnapshot.child("User").child(UID).child("Longitude").getValue().toString());



                double other_latitude = Double.parseDouble(dataSnapshot.child("User").child(d.getKey()).child("Latitude").getValue().toString());
                double other_longitude = Double.parseDouble(dataSnapshot.child("User").child(d.getKey()).child("Longitude").getValue().toString());


                if (this.location!=null&&dataSnapshot.child("User").child(d.getKey()).child("Latitude")!=null){
                    double distance = distance(this_latitude,this_longitude,other_latitude,other_longitude);

                    if (distance<2){
                        nearbyList.add(d.child("Nickname").getValue().toString());
                    }


                }

                if (dataSnapshot.child("User").child(d.getKey()).child("Latitude")!=null){

                }
            }
        }

    }

    public boolean boolConnected(DataSnapshot d){
        return d.child("LoginState").getValue()!=null&&
                d.child("LatestLocation").getValue()!=null&&
                d.child("LoginState").getValue().equals("Online")&&
                d.child("LatestLocation").getValue().equals("1");
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
        FirebaseUser UserInfo =  mAuth.getCurrentUser();
        this.location = location;
        database.child("User").child(UserInfo.getUid()).child("Longitude").setValue(location.getLongitude());
        database.child("User").child(UserInfo.getUid()).child("Latitude").setValue(location.getLatitude());

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


package com.example.pomodoro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PlacesFragment extends Fragment {
    RecyclerView recyclerView;
    String apiKey = "AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
    double latitude;
    double longitude;
    public View mView;
    Context mContext;
    Button newLocation;
    boolean nearbyShown = true;

    //location manager
    private LocationManager lm;
    LocationListener locationListener;
    LocList locationNames = new LocList(getActivity());

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference database;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mView = inflater.inflate(R.layout.activity_places, null);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        Places.initialize(getActivity().getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(getActivity());
        checkGPSSettings();
        updatePlaces();
        locationNames.buildScrollable();


        // Add listener for Add Location button
        final NewLocationFragment selectedFragment = new NewLocationFragment();
        newLocation = mView.findViewById(R.id.addLocation);
        newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", latitude);
                bundle.putDouble("lon", longitude);
                selectedFragment.setArguments(bundle);

                int i = getActivity().findViewById(R.id.fragment_container).getId();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(i, selectedFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        // Add listener for Nearby Libraries button
        newLocation = mView.findViewById(R.id.NearbyLibs);
        newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlaces();
                nearbyShown = true;
            }
        });

        // Add listener for My Spots button
        newLocation = mView.findViewById(R.id.MySpots);
        newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyShown = false;
                displayMySpots();
            }
        });

        return mView;
    }


    private void updatePlaces(){

        //build places query string
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ String.valueOf(latitude) + "," + String.valueOf(longitude) + "&rankby=distance&type=library&name&geometry&key=AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
        final String[] jsonToParse = new String[1];

        // Instantiate the RequestQueue.
        RequestQueue queue;
        queue = Volley.newRequestQueue(mContext);

        // Clear the list of locations before re-building it
        locationNames.clearList();

        // Request a list of locations from Google API as a JSON object, then parse it and add to location list.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, placesSearchStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.i("JSON request: ", "Reply received from Google API");
                            JSONArray array = response.getJSONArray("results");

                            // Iterate through locations, adding them to the location list
                            for (int i = 0; i < array.length(); i++){
                                JSONObject candidate = array.getJSONObject(i);
                                String name = candidate.getString("name");

                                // Get the latitude and longitude of the location
                                JSONObject geometry = candidate.getJSONObject("geometry");
                                JSONObject latlong = geometry.getJSONObject("location");
                                double locLat = latlong.getDouble("lat");
                                double locLon = latlong.getDouble("lng");
                                LatLng loc = new LatLng(locLat, locLon);

                                double distance = distance(latitude, longitude, locLat, locLon);

                                locationNames.addToList(name, loc, distance); // Add name, lat/long of location
                                Log.i("Adding to location list: ", name + " " + String.valueOf(loc));


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Nearby Places","Volley error : Something went wrong", error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private void displayMySpots(){
        // Clear the list of locations before re-building it
        locationNames.clearList();
        // Find "My Places" in firebase database and add to list.
        final ArrayList<String> placeIds = new ArrayList<>();
        database.child("User").child(user.getUid()).child("MyPlaces").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    placeIds.add(String.valueOf(dsp.getValue())); //add result into array list
                    Log.i("place ID", String.valueOf(dsp.getValue()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Find custom places of current user and add to location list.
        database.child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    String curPlaceId = String.valueOf(dsp.getKey());
                    for(int i = 0; i < placeIds.size(); i++){
                        if (curPlaceId.equals(placeIds.get(i))){
                            double curLat = 0;
                            double curLon = 0;
                            Number tempLat = (Number) snapshot.child(curPlaceId).child("Latitude").getValue();
                            Number tempLon = (Number) snapshot.child(curPlaceId).child("Longitude").getValue();
                            String curName = snapshot.child(curPlaceId).child("PlaceName").getValue().toString();

                            if (tempLat != null) {
                                curLat = tempLat.doubleValue();
                            }
                            if (tempLon != null) {
                                curLon = tempLon.doubleValue();
                            }

                            LatLng loc = new LatLng(curLat, curLon);

                            double distance = distance(latitude, longitude, curLat, curLon);
                            locationNames.addToList(curName, loc, distance); // Add name, lat/long of location
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Find the distance between two locations - result in kilometres
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

    //Class for list of nearby locations, updates the  recyclerView when list is changed
    public class LocList{

        private ArrayList<String> locList = new ArrayList<String>();
        private ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
        private ArrayList<Double> distanceList = new ArrayList<Double>();
        NearbyAdapter newAdapter;
        Context context;

        public LocList(Context cnt){
            context = cnt;
        }

        public ArrayList<String> getList() {
            return locList;
        }

        public void addToList(String str, LatLng latLng, double dist){
            locList.add(str);
            latLngList.add(latLng);
            distanceList.add(dist);
            if (newAdapter != null) {
                newAdapter.notifyDataSetChanged();
            }
        }

        public void clearList(){
            locList.clear();
            latLngList.clear();
            distanceList.clear();
            if (newAdapter != null) {
                newAdapter.notifyDataSetChanged();
            }
        }

        // only called when list is first built
        public void buildScrollable(){
            recyclerView = mView.findViewById(R.id.RecyclerView);
            newAdapter = new NearbyAdapter(getActivity(), locList, latLngList, distanceList); // Pass location name + position to layout
            recyclerView.setAdapter(newAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


    }

    // Find location methods - code taken from tutorial
    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void startLocalisation() {

        // parameters of location service
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // cellular or WIFI network can localise me
        String providerNET = LocationManager.NETWORK_PROVIDER;

        // gps signal often naive
        String providerGPS = LocationManager.GPS_PROVIDER;

        // must call this before using getLastKnownLocation
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }

        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled) {
            Log.d("Nearby Places", " gps_enabled");

            //requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener)
            lm.requestLocationUpdates(providerGPS, 10000, 10, locationListener);
            gps_loc = lm.getLastKnownLocation(providerGPS);
        }
        if (network_enabled){
            Log.d("Nearby Places", " net_enabled");
            lm.requestLocationUpdates(providerNET, 10000, 10, locationListener);
            net_loc = lm.getLastKnownLocation(providerNET);
        }

        if (gps_loc != null && net_loc != null) {

            Log.d("Nearby Places", "both available location");

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
                Log.d("Nearby Places", "gps available location");
            } else if (net_loc != null) {
                finalLoc = net_loc;
                Log.d("Nearby Places", "net available location");
            }
        }
//        if (gps_loc != null) {
//                finalLoc = gps_loc;}

        if (finalLoc != null) {

            double latitude = finalLoc.getLatitude();
            double longitude = finalLoc.getLongitude();

            Log.d("Nearby Places", "Found location: latitude：" + latitude + "\nlongitude" + longitude);
            // Update the list of places close by if Nearby Libraries is selected
            if (nearbyShown) {
                updatePlaces();
            };

        } else {
            Log.d("Nearby Places", "no available location");
            //startLocalisation();
        }
    }

    public void checkGPSSettings() {
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("Nearby Places", "Location Changed");
                try {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }catch (Exception e){
                    Log.e("Places", e.getMessage());
                }

                //location.getProvider();
                Log.d("Nearby Places", "" + location.getProvider() + " Found Location: latitude " + latitude + "\nlongitude:" + longitude);
                updatePlaces();
            }
        };

        // Location hardware setting enabled?
        boolean GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String[] permissionsArray = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (GPSEnabled) {
            // Android 6.0+
            if (Build.VERSION.SDK_INT >= 23) {
                if (!checkPermissions(getActivity(), permissionsArray)) {
                    // request code 1

                    Log.d("Nearby Places", "request");

                    ActivityCompat.requestPermissions(getActivity(), permissionsArray,
                            1);
                } else {
                    // Permission has already been granted
                    Log.d("Nearby Places", "line 52");
                    startLocalisation();
                }


            } else {
                // no runtime check
                Log.d("Nearby Places", "line 74");
                startLocalisation();
            }
        } else {
            Log.d("Nearby Places", "line 82");
            Toast.makeText(getActivity(), "GPS Not Enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // request code 2
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // request code 2
            case 2:
                checkGPSSettings();
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            // request code 1
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Log.d("Nearby Places", "line 90");
                    startLocalisation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            default:
                break;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




}
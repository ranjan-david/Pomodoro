package com.example.pomodoro;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String apiKey = "AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
    String lat = "-34";
    String lng = "151";
    //location manager
    private LocationManager locMan;
    LocList locationNames = new LocList(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);
        updatePlaces();
    }

    private void updatePlaces(){

        //build places query string
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-37.802,144.9595&radius=1500&type=library&name&geometry&key=AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
        final String[] jsonToParse = new String[1];

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

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

                                locationNames.addToList(name, loc); // Add name, lat/long of location
                                Log.i("Adding to location list: ", name + " " + String.valueOf(loc));
                            }

                            // Use location list to build a scrollable list
                            locationNames.buildScrollable();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley error ","Something went wrong", error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(request);

    }

    public class LocList{

        //Allows access to the list of nearby locations, updates the recyclerView when list is changed

        private ArrayList<String> locList = new ArrayList<String>();
        private ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
        Context context;

        public LocList(Context cnt){
            context = cnt;
        }

        public ArrayList<String> getList() {
            return locList;
        }

        public void addToList(String str, LatLng latLng){
            locList.add(str);
            latLngList.add(latLng);
        }

        public void clearList(){
            locList.clear();
            latLngList.clear();
        }

        public void buildScrollable(){
            recyclerView = findViewById(R.id.RecyclerView);
            NearbyAdapter newAdapter = new NearbyAdapter(context, locList, latLngList); // Pass location name + position to layout
            recyclerView.setAdapter(newAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

    }


}
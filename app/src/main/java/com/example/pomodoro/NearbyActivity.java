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
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=library&inputtype=textquery&fields=photos,geometry,formatted_address,name,opening_hours,rating&locationbias=circle:2000@47.6918452,-122.2226413&key=AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
        final String[] jsonToParse = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, placesSearchStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("candidates");

                            for (int i = 0; i < array.length(); i++){
                                JSONObject candidate = array.getJSONObject(i);
                                String name = candidate.getString("name");
                                locationNames.addToList(name);
                                Log.i("The Place is called: ", name);

                                JSONObject geometry = candidate.getJSONObject("geometry");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Okay, ","That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(request);

    }

    public class LocList{
        private ArrayList<String> locList = new ArrayList<String>();
        Context context;

        public LocList(Context cnt){
            context = cnt;
        }

        public ArrayList<String> getList() {
            return locList;
        }

        public void addToList(String str){
            locList.add(str);
            recyclerView = findViewById(R.id.RecyclerView);

            MyAdapter newAdapter = new MyAdapter(context, locList); // Pass strings to layout

            recyclerView.setAdapter(newAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

        }

    }


}
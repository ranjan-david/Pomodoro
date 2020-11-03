package com.example.pomodoro;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng position;
    String locName;
    float zoomLevel = 16.0f; // Default zoom level for map camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.AddMap);
        mapFragment.getMapAsync(this);
        Bundle b = getIntent().getExtras();
        Object locPos = b.get("locPos");
        position = (LatLng) locPos;
        locName = b.getString("locName");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * The location position is added as a marker
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at position and move camera
        mMap.addMarker(new MarkerOptions().position(position).title(locName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
    }
}
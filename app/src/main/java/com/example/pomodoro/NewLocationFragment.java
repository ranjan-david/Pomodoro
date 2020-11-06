package com.example.pomodoro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewLocationFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    public View mView;
    FragmentActivity mContext;
    String apiKey = "AIzaSyCpfH3kSTZK-NIbv00PuMCgGMK1YyrY9V8";
    DatabaseReference database;
    double lat;
    double lon;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private GoogleMap mMap;
    float zoomLevel = 16.0f; // Default zoom level for map camera
    LatLng newLoc;
    private Marker marker;
    Button addLocation;
    private EditText locName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mView = inflater.inflate(R.layout.activity_add_loc, null);

        Places.initialize(getActivity().getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(getActivity());

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user =  mAuth.getCurrentUser();

        // Get lat and lon from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            double lat = bundle.getDouble("lat", 0);
            double lon = bundle.getDouble("lon", 0);
        }
        newLoc = new LatLng(lat,lon);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.AddMap);
        mapFragment.getMapAsync(this);

        //Add location to database on click
        addLocation = mView.findViewById(R.id.addLocationText);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Turn lat/lon into location id
                String placeId = String.valueOf(newLoc);
                placeId = placeId.replace(",", "");
                placeId = placeId.replace(".", "");
                placeId = placeId.replace("/", "");

                // New Location to database
                locName = (EditText) mView.findViewById(R.id.editLocName);
                final String finalPlaceId = placeId;
                final Place place = new Place(locName.getText().toString());
                final String finalLocName = locName.getText().toString();

                database.child("Location").child(finalPlaceId).child("PlaceName").setValue(finalLocName);
                database.child("Location").child(finalPlaceId).child("Latitude").setValue(lat);
                database.child("Location").child(finalPlaceId).child("Longitude").setValue(lon);

                database.child("User").child(user.getUid()).child("MyPlaces").child(finalLocName).setValue(placeId);
            }
        });

        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng pos = new LatLng(lat,lon);
        // Add a marker at position and move camera
        mMap.addMarker(new MarkerOptions().position(pos).title("Current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoomLevel));

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        // Add a marker at click location
        newLoc = point;
        lat = point.latitude;
        lon = point.longitude;
        Log.i("New CLick", String.valueOf(lat));
        if (marker != null){
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(point).title("New Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel));
    }

    @Override
    public void onMapLongClick(LatLng point) {
    }

    @Override
    public void onCameraIdle() {
    }


}

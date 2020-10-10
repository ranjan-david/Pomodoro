package com.example.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyViewHolder>{
    ArrayList<String> locationNames;
    ArrayList<LatLng> locationLatLng;
    Context context;
    LatLng locPos;
    String locName;

    public NearbyAdapter(Context ct, ArrayList<String> locNames, ArrayList<LatLng> latLng){
        context = ct;
        locationLatLng = latLng;
        locationNames = locNames;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Sets the updatable fields
        locName = locationNames.get(position);
        locPos = locationLatLng.get(position);
        holder.name.setText(locName);

        final Intent intent = new Intent(context, MapsActivity.class); // Create new activity intent for map view
        intent.putExtra("locName", locName); // Give location name to map activity
        intent.putExtra("locPos", locPos); //Give latitude, longitude to map activity

        //Bind map activity intent to "View on Map" button
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Button mapButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.LocationName);
            mapButton = itemView.findViewById(R.id.MapView);
        }
    }
}

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyViewHolder>{
    ArrayList<String> locationNames;
    ArrayList<LatLng> locationLatLng;
    ArrayList<Double> locationDistance;
    Context context;
    LatLng locPos;
    String locName;
    Double locDist;

    public NearbyAdapter(Context ct, ArrayList<String> locNames, ArrayList<LatLng> latLng, ArrayList<Double> locDist){
        context = ct;
        locationLatLng = latLng;
        locationNames = locNames;
        locationDistance = locDist;
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
        locDist = locationDistance.get(position);
        holder.name.setText(locName);
        DecimalFormat df = new DecimalFormat("#.##"); // Format the distance to 2 decimal places
        holder.distance.setText(String.valueOf(df.format(locDist)) + " km away");

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
        TextView distance;
        Button mapButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.LocationName);
            distance = itemView.findViewById(R.id.Distance);
            mapButton = itemView.findViewById(R.id.MapView);
        }
    }
}

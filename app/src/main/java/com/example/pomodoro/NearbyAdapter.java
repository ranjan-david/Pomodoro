package com.example.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Timestamp;
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
    String checkedIn;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference database;

    public NearbyAdapter(Context ct, ArrayList<String> locNames, ArrayList<LatLng> latLng, ArrayList<Double> locDist){
        context = ct;
        locationLatLng = latLng;
        locationNames = locNames;
        locationDistance = locDist;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //Sets the updatable fields
        locName = locationNames.get(position);
        locPos = locationLatLng.get(position);
        locDist = locationDistance.get(position);
        holder.name.setText(locName);

        // Formatting distance and locPos
        DecimalFormat df = new DecimalFormat("#.##"); // Format the distance to 2 decimal places
        holder.distance.setText(String.valueOf(df.format(locDist)) + " km away");
        String placeId = String.valueOf(locPos);
        placeId = placeId.replace(",", "");
        placeId = placeId.replace(".", "");
        placeId = placeId.replace("/", "");

        // Allows location to be viewed on map
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

        // Add UID and time checked in to Location entry in database
        final String finalPlaceId = placeId;
        final Place place = new Place(locName);
        final String finalLocName = locName;

        // Find where the user is currently checked in

        database.child("User").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                checkedIn = snapshot.child("Location").getValue().toString();
                // Change text if user is checked in
                if (checkedIn.equals(finalPlaceId)){
                    holder.checkIn.setText("Checked In Here");
                }else{
                    holder.checkIn.setText("Check In");
                }
            } @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });

        Log.i("Checked in to ", "somewhere: " + checkedIn);



        holder.checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status =(Integer) v.getTag();

                if (status == 1) {
                    if (checkedIn != null) {
                        database.child("Location").child(checkedIn).child("Checked in").child(user.getUid()).removeValue();
                    }

                    holder.checkIn.setText("Checked In Here");
                    v.setTag(0); // Checked In
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    database.child("Location").child(finalPlaceId).setValue(place);
                    database.child("Location").child(finalPlaceId).child("Checked in").child(user.getUid()).setValue(time);
                    checkedIn = finalPlaceId;
                    database.child("User").child(user.getUid()).child("Location").setValue(finalPlaceId);
                    database.child("User").child(user.getUid()).child("LocationName").setValue(finalLocName);

                }else {
                    holder.checkIn.setText("Check In");
                    database.child("Location").child(finalPlaceId).child("Checked in").child(user.getUid()).removeValue();
                    database.child("User").child(user.getUid()).child("LocationName").removeValue();
                    v.setTag(1); //Checked Out
                }
            }
        });

        // Find the number of users currently checked in somewhere
        database.child("Location").child(finalPlaceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Long number = snapshot.child("Checked in").getChildrenCount();
                if (number != 1){
                    holder.numberChecked.setText(String.valueOf(number) + " people checked in");
                }else {
                    holder.numberChecked.setText(String.valueOf(number) + " person checked in");
                }
            } @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
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
        TextView numberChecked;
        Button mapButton;
        Button checkIn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.LocationName);
            numberChecked = itemView.findViewById(R.id.NumberCheckedIn);
            distance = itemView.findViewById(R.id.Distance);
            mapButton = itemView.findViewById(R.id.MapView);
            checkIn = itemView.findViewById(R.id.CheckIn);
            checkIn.setTag(1);
        }
    }
}

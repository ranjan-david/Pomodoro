package com.example.pomodoro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import java.time.Duration;
import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

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

    //Time managers
    AlarmManager processTimer;
    Intent timeIntent;

    Integer timeSpent;

    public NearbyAdapter(Context ct, ArrayList<String> locNames, ArrayList<LatLng> latLng, ArrayList<Double> locDist){
        context = ct;
        locationLatLng = latLng;
        locationNames = locNames;
        locationDistance = locDist;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        processTimer = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        timeIntent = new Intent(context, processTimerReceiver.class);
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
        final Intent[] intent = {new Intent(context, MapsActivity.class)}; // Create new activity intent for map view
        intent[0].putExtra("locName", locName); // Give location name to map activity
        intent[0].putExtra("locPos", locPos); //Give latitude, longitude to map activity

        //Bind map activity intent to "View on Map" button
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent[0]);
            }
        });

        // Add UID and time checked in to Location entry in database
        final String finalPlaceId = placeId;
        final Place place = new Place(locName);
        final String finalLocName = locName;

        // Listener to find where the user is currently checked in
        database.child("User").child(user.getUid()).child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                checkedIn = snapshot.getValue().toString();
                // Change text if user is checked in
                if (checkedIn.equals(finalPlaceId)){
                    holder.checkIn.setText("Check Out");
                    holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }else{
                    holder.checkIn.setText("Check In");
                    holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            } @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });

        // Listener to find the number of users currently checked in somewhere
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

        // Listener to get time user has spent at this location
        database.child("User").child(user.getUid()).child("LocationTimes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    timeSpent = snapshot.child(finalPlaceId).getValue(Integer.class);
                    if (timeSpent != null){
                        holder.studyTime.setText(String.valueOf(timeSpent) + " mins");
                    } else{
                        holder.studyTime.setText("0 mins");
                    }
                }catch(Exception e){
                }
            } @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Wrong");
            }
        });

        // Update database, change text and background colour when "Check In" button is pressed
        final PendingIntent[] pendingIntent = new PendingIntent[1];
        holder.checkIn.setOnClickListener(new View.OnClickListener() {
            Intent timerIntent = new Intent();

            @Override
            public void onClick(View v) {
                final int status =(Integer) v.getTag();

                //Params for timer
                timeIntent.putExtra("Place", finalPlaceId);
                pendingIntent[0] = PendingIntent.getBroadcast(context, 0,  timeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                //Check button status 1=not checked in, 0 =checked in
                if (status == 1) {
                    if (checkedIn != null) {
                        // Check out of previous location
                        database.child("Location").child(checkedIn).child("Checked in").child(user.getUid()).removeValue();
                    }

                    //Update UI
                    holder.checkIn.setText("Check Out");
                    holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    // Checked In flag
                    v.setTag(0);

                    // Update local checked in value, update remote current location values
                    checkedIn = finalPlaceId;
                    database.child("Location").child(checkedIn).child("Checked in").child(user.getUid()).setValue("Checked In");
                    database.child("User").child(user.getUid()).child("Location").setValue(finalPlaceId);
                    database.child("User").child(user.getUid()).child("LocationName").setValue(finalLocName);

                    // Ensure there is a time value for the checked in location
                    if (timeSpent == null){
                        database.child("User").child(user.getUid()).child("LocationTimes").child(finalPlaceId).setValue(0);
                    }

                    // Start timer using the AlarmManager
                    //Update the time spent in a location every minute
                    Log.i("Nearby Adapter", "Started Timer");
                    processTimer.setRepeating(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),60000, pendingIntent[0]);

                }else {
                    holder.checkIn.setText("Check In");

                    //Update the background colour of the card
                    holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    database.child("Location").child(finalPlaceId).child("Checked in").child(user.getUid()).removeValue();
                    database.child("User").child(user.getUid()).child("LocationName").removeValue();

                    //Stop timer
                    Log.i("NearbyAdapter", "Timer stopped");
                    processTimer.cancel(pendingIntent[0]);

                    //Checked Out flag set
                    v.setTag(1);
                }
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
        TextView studyTime;
        Button mapButton;
        Button checkIn;
        ConstraintLayout card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.LocationName);
            numberChecked = itemView.findViewById(R.id.NumberCheckedIn);
            distance = itemView.findViewById(R.id.Distance);
            mapButton = itemView.findViewById(R.id.MapView);
            checkIn = itemView.findViewById(R.id.CheckIn);
            card = itemView.findViewById(R.id.Card);
            checkIn.setTag(1);
            studyTime = itemView.findViewById(R.id.timeStudied);
        }
    }
}

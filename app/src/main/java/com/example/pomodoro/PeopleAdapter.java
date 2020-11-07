package com.example.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.cylerViewHolder>{
    public static String Cname;
    public static String Ctime;
    // create the holder class
    public static class cylerViewHolder extends RecyclerView.ViewHolder{
        // name,button and challengetime should be update by adapter
        public final TextView title;
        public final TextView Challenge_time;
        public Button button;


        public cylerViewHolder(View v) {
            //get the xml
            super(v);
            title = (TextView) v.findViewById(R.id.nickname);
            button = (Button)v.findViewById(R.id.challenge);
            Challenge_time = (TextView) v.findViewById(R.id.Ctime);
        }
    }

    private List<String> mDatas;
    private List<Integer> challengeTimes;
    //initialize the adapter by nickname list and challenge time list from PeopleFragment
    public PeopleAdapter(Context context, List<String> data,List<Integer> challengeTime) {
        this.mDatas = data;
        this.challengeTimes = challengeTime;
    }


    @NonNull
    @Override

    public void onBindViewHolder(@NonNull cylerViewHolder holder, int position) {
        //set the name and challenge time of user on holder
        holder.title.setText(mDatas.get(position));
        Integer challenge_time = challengeTimes.get(position);
        holder.Challenge_time.setText(challenge_time.toString()+" "+"minutes");

        //these variables will be transmitted to challenge class
        Cname = holder.title.getText().toString();
        Ctime = challenge_time.toString();
        holder.itemView.findViewById(R.id.challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //challenge class
                Intent intent = new Intent (v.getContext(), Challenge.class);
                // transmit variables to challenge class
                String message = Cname;
                String Time = Ctime;

                intent.putExtra("Name", message);
                intent.putExtra("Time", Time);
                v.getContext().startActivity(intent);






            }
        });


    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    // invoke when holder is created
    public cylerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // load the view from people_nearby.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_nearby, parent, false);
        return new cylerViewHolder(v);

    }

}



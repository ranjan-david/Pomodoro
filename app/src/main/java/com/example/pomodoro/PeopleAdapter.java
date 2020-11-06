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
    public static class cylerViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final TextView Challenge_time;
        public Button button;
        public cylerViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.nickname);
            button = (Button)v.findViewById(R.id.challenge);
            Challenge_time = (TextView) v.findViewById(R.id.Ctime);
        }
    }

    private List<String> mDatas;
    public PeopleAdapter(Context context, List<String> data) {
        this.mDatas = data;
    }


    @NonNull
    @Override

    public void onBindViewHolder(@NonNull cylerViewHolder holder, int position) {
        holder.title.setText(mDatas.get(position));
        Cname = holder.title.getText().toString();
        // time of the challenge shown
//        Ctime = holder.Challenge_time.getText().toString();
        holder.itemView.findViewById(R.id.challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (v.getContext(), Challenge.class);

                String message = Cname;
                String Time = Ctime;

                intent.putExtra("Name", message);
//                intent.putExtra("Time", Time);
                v.getContext().startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public cylerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_nearby, parent, false);
        return new cylerViewHolder(v);

    }

}



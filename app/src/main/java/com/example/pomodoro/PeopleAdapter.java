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

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.cylerViewHolder>{
    public static class cylerViewHolder extends RecyclerView.ViewHolder{
        public final TextView title;
        public Button button;
        public cylerViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.nickname);
            button = (Button)v.findViewById(R.id.challenge);
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
        holder.itemView.findViewById(R.id.challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Challenge function can be invoked by click challenge button by this function */

//                Intent intent=new Intent(PeopleAdapter.class,Challenge.class);
//                startActivity(intent);

// public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

//                Intent intent = new Intent(this, Challenge.class);
//                EditText editText = (EditText) findViewById(R.id.nickname);
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
//                startActivity(intent);
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



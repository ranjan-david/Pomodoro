package com.example.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PomodoroFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        final EditText studyTime;
        studyTime=(EditText) view.findViewById(R.id.studyTimeVal);
        studyTime.setInputType(InputType.TYPE_CLASS_NUMBER);


        final EditText shortBreakTime;
        shortBreakTime=(EditText) view.findViewById(R.id.shrtBreakTimeVal);
        shortBreakTime.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText longBreakTime;
        longBreakTime=(EditText) view.findViewById(R.id.lngBreakTimeVal);
        longBreakTime.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText repeatsTillLongBreak;
        repeatsTillLongBreak=(EditText) view.findViewById(R.id.repeatsVal);
        repeatsTillLongBreak.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button button = (Button) view.findViewById(R.id.startPomoButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("POMODOROService", "Button Clicked");

                Intent myIntent = new Intent(getActivity(), activity_do_pomodoro.class);
                myIntent.putExtra("studyTime", Integer.parseInt(studyTime.getText().toString()));
                myIntent.putExtra("shortBreakTime", Integer.parseInt(shortBreakTime.getText().toString()));
                myIntent.putExtra("longBreakTime", Integer.parseInt(longBreakTime.getText().toString()));
                myIntent.putExtra("repeatsTillLongBreak", Integer.parseInt(repeatsTillLongBreak.getText().toString()));
                startActivity(myIntent);
            }
        });

        return view;
    }
}

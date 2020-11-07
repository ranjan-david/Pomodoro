package com.example.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

            int[] inputValues = validateFields(studyTime.getText().toString(), shortBreakTime.getText().toString(), longBreakTime.getText().toString(), repeatsTillLongBreak.getText().toString());

            if(inputValues != null)
            {
                Intent myIntent = new Intent(getActivity(), DoPomodoroActivity.class);
                myIntent.putExtra("studyTime", inputValues[0]);
                myIntent.putExtra("shortBreakTime", inputValues[1]);
                myIntent.putExtra("longBreakTime", inputValues[2]);
                myIntent.putExtra("repeatsTillLongBreak", inputValues[3]);
                startActivity(myIntent);
            }
            else
            {
                Toast.makeText(getActivity(), "Please Provide Valid Input", Toast.LENGTH_SHORT).show();
            }
        }
    });

        return view;
    }

    private int[] validateFields(String studyTime, String shortBreakTime, String longBreakTime, String repeatsTillLongBreak)
    {
        try {
            int studyTimeInt = Integer.parseInt(studyTime);
            int shortBreakTimeInt = Integer.parseInt(studyTime);
            int longBreakTimeInt = Integer.parseInt(studyTime);
            int repeatsTillLongBreakInt = Integer.parseInt(studyTime);

            return new int[]{studyTimeInt, shortBreakTimeInt, longBreakTimeInt, repeatsTillLongBreakInt};
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}

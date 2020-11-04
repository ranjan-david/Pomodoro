package com.example.pdf_maker;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;


public interface ISettingsFragment {
	boolean save(@NonNull SharedPreferences.Editor editor);
}

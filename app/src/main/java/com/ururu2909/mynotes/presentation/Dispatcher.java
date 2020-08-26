package com.ururu2909.mynotes.presentation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.presentation.mainscreen.MainActivity;

public class Dispatcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class<?> activityClass;
        int noteListId = -1;

        try {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE);
            activityClass = Class.forName(
                prefs.getString("last_activity", MainActivity.class.getName()));
            noteListId = prefs.getInt("note_list_id", -1);
        } catch(ClassNotFoundException ex) {
            activityClass = MainActivity.class;
        }

        Intent intent = new Intent(this, activityClass);
        if (noteListId != -1){
            intent.putExtra("note_list_id", noteListId);
        }
        startActivity(intent);
    }
}

package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MoodEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            // TODO: Handle saving logic, including connecting with database
            finish(); // Closes activity and returns to previous screen
        });

        findViewById(R.id.imagebutton_activitymoodeventdetails_backbutton).setOnClickListener(v -> finish()); // Back to previous screen when you hit back button
    }
}


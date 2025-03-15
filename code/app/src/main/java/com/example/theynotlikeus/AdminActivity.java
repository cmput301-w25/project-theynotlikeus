package com.example.theynotlikeus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * View: Handles UI for the admin panel.
 * Controller: Uses MoodController to manage settings.
 */
public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";
    private MoodController moodController;
    private Switch limitSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize controller
        moodController = new MoodController();

        // Find UI elements
        limitSwitch = findViewById(R.id.switch1);
        logoutButton = findViewById(R.id.button_logoutButton);

        // Load saved switch state from SharedPreferences using MoodController
        boolean isLimitEnabled = moodController.isLimitEnabled(this);
        limitSwitch.setChecked(isLimitEnabled);

        // Toggle switch listener
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LIMIT_ON, isChecked);
            editor.apply();

            Toast.makeText(this, isChecked ?
                            "Image size limit enabled (65536 bytes max)" :
                            "Image size limit disabled",
                    Toast.LENGTH_SHORT).show();
        });

        // Logout button listener
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

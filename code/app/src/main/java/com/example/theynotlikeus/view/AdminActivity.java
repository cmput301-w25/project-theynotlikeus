package com.example.theynotlikeus.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theynotlikeus.R;

public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";
    private Switch limitSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Find switch
        limitSwitch = findViewById(R.id.switch1);

        // Load saved switch state from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLimitEnabled = prefs.getBoolean(LIMIT_ON, true);
        limitSwitch.setChecked(isLimitEnabled);

        //Listener for toggle switch
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save new switch state in SharedPreferences

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LIMIT_ON, isChecked);
            editor.apply();

            // Toast a message to notify
            Toast.makeText(this, isChecked ?
                            "Image size limit enabled (65536 bytes max)" :
                            "Image size limit disabled",
                    Toast.LENGTH_SHORT).show();
        });

        // Ensure window insets are properly applied
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Find logout button
        logoutButton = findViewById(R.id.button_logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Redirect to LoginActivity
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
            startActivity(intent);
            finish();
        });
    }

    // Public method to check if limit is enabled (other classes can use this)
    public static boolean isLimitEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(LIMIT_ON, true);
    }
}

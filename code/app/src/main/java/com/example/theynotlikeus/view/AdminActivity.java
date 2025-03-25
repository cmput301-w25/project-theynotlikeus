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
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * This activity provides the interface for admin users, implements UI experience and adjusts
 * the layout to account for system insets (such as the status bar and navigation bar) to allows admin users
 */
public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";
    private MaterialSwitch limitSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        limitSwitch = findViewById(R.id.switch_ActivityAdmin_materialSwitch);

        //Load saved switch state from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLimitEnabled = prefs.getBoolean(LIMIT_ON, true);
        limitSwitch.setChecked(isLimitEnabled);

        //Listener for toggle switch
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Save new switch state in SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LIMIT_ON, isChecked);
            editor.apply();

            //Toast a message to notify
            Toast.makeText(this, isChecked ?
                            "Image size limit enabled (65536 bytes max)" :
                            "Image size limit disabled",
                    Toast.LENGTH_SHORT).show();
        });

        //Ensure window insets are properly applied
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutButton = findViewById(R.id.button_logoutButton); //logout button
        logoutButton.setOnClickListener(v -> {
            //Redirect to LoginActivity
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
            startActivity(intent);
            finish();
        });
    }

    //Public method to check if limit is enabled
    public static boolean isLimitEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(LIMIT_ON, true);
    }
}

package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * This activity provides the interface for admin users, implements UI experience and adjusts
 * the layout to account for system insets (such as the status bar and navigation bar) to allows admin users
 */

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); //Enable immersive edge-to-edge UI.
        setContentView(R.layout.activity_admin);

        //Adjusting the layout's padding to accommodate system bars using WindowInsets.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); //Retrieving the insets for system bars.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); //Setting padding on the view to ensure no UI elements are obscured.
            return insets;
        });
    }
}

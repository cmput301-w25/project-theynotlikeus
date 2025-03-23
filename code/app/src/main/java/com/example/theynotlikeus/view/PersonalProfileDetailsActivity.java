package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theynotlikeus.R;

/**
 * Activity that displays the user's personal profile details.
 *
 * It also:
 * Adjusts the layout to account for system insets (such as status and navigation bars).
 * Provides a back button (using a Toolbar) for easy navigation to the previous screen.
 */
public class PersonalProfileDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_profile_details);

        //Applying system insets to the main view so that UI elements do not overlap system bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Set up the back button using a Toolbar.
        Toolbar backButton = findViewById(R.id.button_PersonalProfileDetailsActivity_back);
        //Set a click listener to finish the activity when the back button is pressed.
        backButton.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //Closes current activity and returns to the previous one
            }
        });
    }
}

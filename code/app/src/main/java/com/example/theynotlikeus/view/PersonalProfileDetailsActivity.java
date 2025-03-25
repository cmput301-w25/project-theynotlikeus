package com.example.theynotlikeus.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.singleton.MyApp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

public class PersonalProfileDetailsActivity extends AppCompatActivity {

    private MaterialTextView textViewUsername;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile_details);

        // Initialize views using Material Components
        toolbar = findViewById(R.id.button_PersonalProfileDetailsActivity_back);
        textViewUsername = findViewById(R.id.textView_username);

        // Set up the toolbar's back navigation
        toolbar.setNavigationOnClickListener(v -> finish());

        // Retrieve the username from the Application singleton
        MyApp myApp = (MyApp) getApplication();
        String username = myApp.getUsername();

        // Display the username (or a fallback message if not available)
        if (username != null && !username.isEmpty()) {
            textViewUsername.setText("Username: " + username);
        } else {
            textViewUsername.setText("Username: Unknown");
        }
    }
}

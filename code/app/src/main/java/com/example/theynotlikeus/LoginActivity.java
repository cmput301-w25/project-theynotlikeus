package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
* Manages user login.
* Stores the logged-in username.
* Sets up the UI with edge-to-edge support.
* */
public class LoginActivity extends AppCompatActivity {
    private static String loggedInUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            loggedInUsername = intent.getStringExtra("username");
            //pass username
        }
    }
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }//get current logged in username
}
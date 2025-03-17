package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theynotlikeus.R;

/**
 * Manages user login and stores the logged-in username.
 *
 * It also:
 * Receives the username from a previous login process via an Intent.
 * Adjusts the layout to account for system insets (e.g., status bar, navigation bar).
 */
public class LoginActivity extends AppCompatActivity {
    private static String loggedInUsername; //Static variable to store the currently logged-in username.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //Set the content view to the login layout.
        setContentView(R.layout.activity_login);
        //Apply system insets so UI elements are properly padded and not obscured by system bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Retrieve the Intent that started this activity.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            loggedInUsername = intent.getStringExtra("username"); //The username is now stored and can be accessed using getLoggedInUsername()
        }
    }

    /**
     * Returns the username of the currently logged-in user.
     *
     * @return the logged-in username.
     */
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }//get current logged in username
}
package com.example.theynotlikeus.view;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.theynotlikeus.singleton.MyApp;



import com.example.theynotlikeus.R;
import com.example.theynotlikeus.notifications.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
/*
 * This activity is the main entry point of the application and it
 *  sets up and initializes bottom navigation and navigation controller for fragment navigation.
 */

/**
 * This activity allows the user to navigate between pages from the home page. (Add mood event, community, profile, etc.)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupNotificationListener();

        // Request POST_NOTIFICATIONS permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }


        // Create notification channel for push notifications.
        // This ensures that notifications sent from the app will be displayed on devices running Android O (API 26) or later.
        NotificationHelper.createNotificationChannel(this);


        ////Apply system insets to the main view to ensure UI elements are not overlapped by system bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ////Initialize the BottomNavigationView for navigation.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Retrieve the NavHostFragment that contains the navigation graph.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView_MainActivity);


        //If the NavHostFragment is successfully retrieved, setup navigation with the BottomNavigationView.
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        /*
        // Check for an extra that indicates which fragment to load
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if ("HomeMyMoodsFrag".equals(fragmentToLoad) && navController != null) {
            // Assumes the destination id for HomeMyMoodsFrag is defined as R.id.HomeMyMoodsFrag in your navigation graph
            bottomNavigationView.setSelectedItemId(R.id.HomeMyMoodsFrag);
        }
*/

    }
    private void setupNotificationListener() {
        // Retrieve the current user's id (or username) from your Application singleton.
        String userId = MyApp.getInstance().getUsername();
        // If for some reason the username is still null, you can provide a fallback.
        if (userId == null || userId.isEmpty()) {
            userId = "defaultUser";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("notifications")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("MainActivity", "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        snapshots.getDocumentChanges().forEach(change -> {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                String title = change.getDocument().getString("title");
                                String message = change.getDocument().getString("message");
                                // Show a local notification.
                                NotificationHelper.sendNotification(MainActivity.this, title, message);
                                // Optionally, delete the notification document after handling.
                                change.getDocument().getReference().delete();
                            }
                        });
                    }
                });
    }

}

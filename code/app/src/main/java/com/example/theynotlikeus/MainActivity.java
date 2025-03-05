package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
/*
 * This activity is the main entry point of the application and it
 *  sets up and initializes bottom navigation and navigation controller for fragment navigation.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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

}

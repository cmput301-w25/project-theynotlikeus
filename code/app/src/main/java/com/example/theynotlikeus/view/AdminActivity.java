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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapter.ApproveMoodAdapter;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";

    private Switch limitSwitch;
    private Button logoutButton;
    private RecyclerView moodsRecyclerView;
    private ApproveMoodAdapter adapter;
    private List<Mood> moodList;
    private MoodController moodController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Bind views.
        limitSwitch = findViewById(R.id.switch1);
        logoutButton = findViewById(R.id.button_logoutButton);
        moodsRecyclerView = findViewById(R.id.recyclerview_adminPage_moodApproval);

        // Load saved switch state.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLimitEnabled = prefs.getBoolean(LIMIT_ON, true);
        limitSwitch.setChecked(isLimitEnabled);

        // Listener for toggle switch.
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LIMIT_ON, isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ?
                    "Image size limit enabled (65536 bytes max)" :
                    "Image size limit disabled", Toast.LENGTH_SHORT).show();
        });

        Button setTriggerWordsButton = findViewById(R.id.button_adminPage_setTriggerWords);
        setTriggerWordsButton.setOnClickListener(v -> {
            // Perform a fragment transaction to display TriggerWordsFrag.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TriggerWordsFrag())
                    .addToBackStack(null)
                    .commit();
        });

        // Adjust layout for system insets.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutButton = findViewById(R.id.button_logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize the MoodController.
        moodController = new MoodController();

        // Initialize the RecyclerView and its adapter.
        moodList = new ArrayList<>();
        adapter = new ApproveMoodAdapter(moodList, new ApproveMoodAdapter.OnMoodActionListener() {
            @Override
            public void onApprove(Mood mood) {
                // Set pendingReview to false to approve the mood.
                mood.setPendingReview(false);
                moodController.updateMood(mood, () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Approved mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    loadPendingMoods();
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error approving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }

            @Override
            public void onDelete(Mood mood) {
                // Delete the mood from Firestore.
                moodController.deleteMood(mood.getDocId(), () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Deleted mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    loadPendingMoods();
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }
        });
        moodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moodsRecyclerView.setAdapter(adapter);

        // Load all pending moods.
        loadPendingMoods();
    }

    /**
     * Loads moods from Firestore that are pending review (i.e. pendingReview == true)
     * and sorts them in descending order by date.
     */
    private void loadPendingMoods() {
        moodController.getAllMoods(moods -> {
            List<Mood> pendingMoods = new ArrayList<>();
            for (Mood mood : moods) {
                if (mood.isPendingReview()) {
                    pendingMoods.add(mood);
                }
            }
            // Sort pending moods by date (most recent first).
            Collections.sort(pendingMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
            runOnUiThread(() -> adapter.updateMoodList(pendingMoods));
        }, exception -> runOnUiThread(() ->
                Toast.makeText(AdminActivity.this, "Error loading moods: " + exception.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    // Public method for other classes to check if the image size limit is enabled.
    public static boolean isLimitEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(LIMIT_ON, true);
    }
}

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
import com.example.theynotlikeus.adapters.ApproveMoodAdapter;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.notifications.NotificationHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Create notification channel for admin notifications.
        NotificationHelper.createNotificationChannel(this);

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
                mood.setPendingReview(false);
                moodController.updateMood(mood, () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Approved mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    // The real-time listener will update the list automatically.
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error approving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }

            @Override
            public void onDelete(Mood mood) {
                moodController.deleteMood(mood.getDocId(), () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Deleted mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    // The real-time listener will update the list automatically.
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }
        });
        moodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moodsRecyclerView.setAdapter(adapter);

        // Start listening for real-time updates.
        loadPendingMoods();
    }

    /**
     * Listens for real-time updates of moods and filters for those pending review.
     */
    private void loadPendingMoods() {
        // Listen in real time for moods with pendingReview == true.
        FirebaseFirestore.getInstance()
                .collection("moods")
                .whereEqualTo("pendingReview", true)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        runOnUiThread(() -> Toast.makeText(AdminActivity.this,
                                "Error loading moods: " + error.getMessage(), Toast.LENGTH_SHORT).show());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        List<Mood> pendingMoods = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Mood mood = doc.toObject(Mood.class);
                            if (mood != null) {
                                mood.setDocId(doc.getId());
                                pendingMoods.add(mood);
                            }
                        }
                        // Sort moods in descending order by date.
                        Collections.sort(pendingMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
                        runOnUiThread(() -> {
                            adapter.updateMoodList(pendingMoods);
                            // If there are any pending moods, send a notification to the admin.
                            if (!pendingMoods.isEmpty()) {
                                NotificationHelper.sendNotification(
                                        AdminActivity.this,
                                        "Mood Review Needed",
                                        "There are " + pendingMoods.size() + " mood events pending review."
                                );
                            }
                        });
                    }
                });
    }


    // Public method for other classes to check if the image size limit is enabled.
    public static boolean isLimitEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(LIMIT_ON, true);
    }
}

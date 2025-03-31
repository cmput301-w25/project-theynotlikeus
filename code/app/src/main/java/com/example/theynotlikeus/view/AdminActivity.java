package com.example.theynotlikeus.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminActivity provides the administrative interface of the app.
 * Admins can review mood entries submitted by users, approve or delete them,
 * toggle the image size limit setting, manage trigger words, and send notifications.
 * This activity also listens for real-time changes in the Firestore database to reflect
 * the latest moods that are pending approval.
 */

public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    private Switch limitSwitch;
    private Button logoutButton;
    private RecyclerView moodsRecyclerView;
    private ApproveMoodAdapter adapter;
    private List<Mood> moodList;
    private MoodController moodController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the admin dashboard with UI components, listeners, and real-time mood updates.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Set up the notification channel and permissions.
        NotificationHelper.createNotificationChannel(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        //Bind views.
        limitSwitch = findViewById(R.id.switch1);
        logoutButton = findViewById(R.id.button_logoutButton);
        moodsRecyclerView = findViewById(R.id.recyclerview_adminPage_moodApproval);

        //Load the saved state of the image size limit toggle.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLimitEnabled = prefs.getBoolean(LIMIT_ON, true);
        limitSwitch.setChecked(isLimitEnabled);

        //Toggle listener to save image size limit preference.
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LIMIT_ON, isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ?
                    "Image size limit enabled (65536 bytes max)" :
                    "Image size limit disabled", Toast.LENGTH_SHORT).show();
        });

        //Open fragment to set trigger words.
        Button setTriggerWordsButton = findViewById(R.id.button_adminPage_setTriggerWords);
        setTriggerWordsButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TriggerWordsFrag())
                    .addToBackStack(null)
                    .commit();
        });

        //Handle system window insets for better layout.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Logout button functionality.
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        //Initialize controller and adapter for the moods RecyclerView.
        moodController = new MoodController();
        moodList = new ArrayList<>();
        adapter = new ApproveMoodAdapter(moodList, new ApproveMoodAdapter.OnMoodActionListener() {
            @Override
            public void onApprove(Mood mood) {
                //Mark the mood as reviewed and update in Firestore.
                mood.setPendingReview(false);
                moodController.updateMood(mood, () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Approved mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    sendNotificationToUser(mood.getUsername(), "Mood Approved", "Your mood event was approved by the admin.");
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error approving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }

            @Override
            public void onDelete(Mood mood) {
                //Delete mood entry from Firestore.
                moodController.deleteMood(mood.getDocId(), () -> runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "Deleted mood from " + mood.getUsername(), Toast.LENGTH_SHORT).show();
                    sendNotificationToUser(mood.getUsername(), "Mood Deleted", "Your mood event was deleted by the admin.");
                }), e -> runOnUiThread(() ->
                        Toast.makeText(AdminActivity.this, "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
            }
        });

        moodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moodsRecyclerView.setAdapter(adapter);

        //Begin listening for real-time changes in mood submissions.
        listenForPendingMoods();
    }

    /**
     * Sends a Firestore-based notification to a user by writing a document
     * to the "notifications" collection.
     *
     * @param userId  ID or username of the recipient user.
     * @param title   Notification title.
     * @param message Notification message.
     */
    private void sendNotificationToUser(String userId, String title, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("notifications").add(notificationData)
                .addOnSuccessListener(documentReference ->
                        Log.d("AdminActivity", "Notification document written: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e("AdminActivity", "Error writing notification", e));
    }

    /**
     * Attaches a Firestore snapshot listener that fetches moods pending approval,
     * updates the UI in real time, and notifies the admin if any are found.
     */
    private void listenForPendingMoods() {
        db.collection("moods")
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
                        //Sort moods by most recent first.
                        Collections.sort(pendingMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
                        runOnUiThread(() -> {
                            adapter.updateMoodList(pendingMoods);
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

    /**
     * Returns the current state of the image size limit preference.
     *
     * @param prefs SharedPreferences object to read from.
     * @return true if the image size limit is enabled; false otherwise.
     */
    public static boolean isLimitEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(LIMIT_ON, true);
    }

    /**
     * Handles the result of the notification permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications permission denied. Admin may not receive notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
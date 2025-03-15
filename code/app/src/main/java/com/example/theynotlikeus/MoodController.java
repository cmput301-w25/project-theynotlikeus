package com.example.theynotlikeus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller class to manage Mood objects.
 */
public class MoodController {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String LIMIT_ON = "limit_on";
    private FirebaseFirestore db;

    public MoodController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves the image size limit setting from SharedPreferences.
     */
    public boolean isLimitEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(LIMIT_ON, true);
    }

    /**
     * Loads moods from Firestore and applies the size limit setting.
     */
    public void loadMoods(Context context, String username, Consumer<List<Mood>> callback) {
        boolean isLimitEnabled = isLimitEnabled(context);

        db.collection("moods")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("MoodController", "Error fetching moods", task.getException());
                        return;
                    }

                    List<Mood> moods = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Mood mood = snapshot.toObject(Mood.class);
                        mood.setLimitEnabled(isLimitEnabled); // Apply setting
                        moods.add(mood);
                    }

                    callback.accept(moods); // Return moods to the caller
                });
    }
}

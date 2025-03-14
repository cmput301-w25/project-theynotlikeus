package com.example.theynotlikeus.controller;

import android.os.Build;
import android.util.Log;

import com.example.theynotlikeus.Mood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller handles CRUD operations for mood objects ine the Firestore database.
 */
public class MoodController {
    private final FirebaseFirestore db;

    public MoodController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a new mood to the firestore database
     *
     * @param mood  Mood object to be added.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     *
     */
    public void addMood(Mood mood, Runnable onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods")
                .add(mood)
                .addOnSuccessListener(documentReference -> onSuccess.run())
                .addOnFailureListener((OnFailureListener) onFailure);

    }

    /**
     * Updates existing moods from the database.
     *
     * @param mood  Mood object with updated data.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure
     */
    public void updateMood(Mood mood, Runnable onSuccess, Consumer<Exception> onFailure) {
        if (mood.getDocId() == null || mood.getDocId().isEmpty()) {
            Log.e("MoodController", "updateMood: Document ID is null or empty");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                onFailure.accept(new IllegalArgumentException("Invalid document ID"));
            }
            return;
        }

        db.collection("moods").document(mood.getDocId())
                .set(mood)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener((OnFailureListener) onFailure);
    }

    /**
     * Deletes a mood from Firestore.
     *
     * @param moodId   The ID of the mood document to delete.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     */
    public void deleteMood(String moodId, Runnable onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods").document(moodId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener((OnFailureListener) onFailure);
    }

    /**
     * Fetches a single mood from Firestore.
     *
     * @param moodId   The ID of the mood document to retrieve.
     * @param onSuccess Callback for returning the Mood object.
     * @param onFailure Callback for handling errors.
     */
    public void getMood(String moodId, Consumer<Mood> onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods").document(moodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Mood mood = documentSnapshot.toObject(Mood.class);
                        if (mood != null) {
                            mood.setDocId(documentSnapshot.getId());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                onSuccess.accept(mood);
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                onFailure.accept(new Exception("Mood data is null"));
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            onFailure.accept(new Exception("Mood not found"));
                        }
                    }
                })
                .addOnFailureListener((OnFailureListener) onFailure);
    }

    /**
     * Fetches all moods for a specific user.
     *
     * @param username The username to filter moods.
     * @param onSuccess Callback for returning a list of moods.
     * @param onFailure Callback for handling errors.
     */
    public void getMoodsByUser(String username, Consumer<List<Mood>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Mood> moods = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Mood mood = document.toObject(Mood.class);
                        mood.setDocId(document.getId());
                        moods.add(mood);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onSuccess.accept(moods);
                    }
                })
                .addOnFailureListener((OnFailureListener) onFailure);
    }
}

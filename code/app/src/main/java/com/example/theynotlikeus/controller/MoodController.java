package com.example.theynotlikeus.controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller handles CRUD operations for mood objects in the Firestore database.
 */
public class MoodController extends FirebaseController {
    private final FirebaseFirestore db;

    public MoodController() {
        super();
        this.db = super.getFirebase();
    }

    /**
     * Adds a new mood to the Firestore database.
     *
     * Using addOnCompleteListener ensures that when offline,
     * the local write is committed and the callback is fired immediately.
     *
     * @param mood      Mood object to be added.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     */
    public void addMood(Mood mood, Runnable onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods")
                .add(mood)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && task.getException() != null) {
                            onFailure.accept(task.getException());
                        }
                    }
                });
    }

    /**
     * Updates an existing mood in the database.
     *
     * @param mood      Mood object with updated data.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && task.getException() != null) {
                            onFailure.accept(task.getException());
                        }
                    }
                });
    }

    /**
     * Deletes a mood from Firestore.
     *
     * @param moodId    The ID of the mood document to delete.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     */
    public void deleteMood(String moodId, Runnable onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods").document(moodId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && task.getException() != null) {
                            onFailure.accept(task.getException());
                        }
                    }
                });
    }

    /**
     * Fetches a single mood from Firestore.
     *
     * @param moodId    The ID of the mood document to retrieve.
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
                            onSuccess.accept(mood);
                        } else {
                            onFailure.accept(new Exception("Mood data is null"));
                        }
                    } else {
                        onFailure.accept(new Exception("Mood not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }

    /**
     * Attaches a realtime listener to fetch all moods for a specific user.
     * This listener returns cached data immediately (if available) and updates when the network changes.
     *
     * @param username  The username to filter moods.
     * @param onUpdate  Callback that receives the list of Mood objects.
     * @param onFailure Callback for handling errors.
     */
    public void listenMoodsByUser(String username, Consumer<List<Mood>> onUpdate, Consumer<Exception> onFailure) {
        db.collection("moods")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        onFailure.accept(error);
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Mood> moods = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Mood mood = document.toObject(Mood.class);
                            mood.setDocId(document.getId());
                            moods.add(mood);
                        }
                        onUpdate.accept(moods);
                    }
                });
    }

    /**
     * Fetches all moods for a specific user using a one-time get (not realtime).
     *
     * @param username  The username to filter moods.
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
                    onSuccess.accept(moods);
                })
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }
}

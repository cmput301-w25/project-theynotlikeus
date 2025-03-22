package com.example.theynotlikeus.controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller handles CRUD operations for mood objects in the Firestore database.
 */
public class MoodController extends FirebaseController {
    private final FirebaseFirestore db;
    private ListenerRegistration registrationHolder = null;
    private DocumentReference docRef;

    public MoodController() {
        super();
        this.db = super.getFirebase();
    }

    /**
     * Adds a new mood to the Firestore database.
     *
     * @param mood      Mood object to be added.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     */
    public void addMood(Mood mood, Runnable onSuccess, Consumer<Exception> onFailure) {
        docRef = db.collection("moods").document();

        registrationHolder = docRef.addSnapshotListener(
                MetadataChanges.INCLUDE,
                (snapshot, error) -> {
                    if (error != null) {
                        if (registrationHolder != null) {
                            registrationHolder.remove();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            onFailure.accept(error);
                        }
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        if (registrationHolder != null) {
                            registrationHolder.remove();
                        }
                        onSuccess.run();
                    }
                }
        );

        docRef.set(mood).addOnFailureListener(e -> {
            if (registrationHolder != null) {
                registrationHolder.remove();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                onFailure.accept(e);
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

        docRef = db.collection("moods").document(mood.getDocId());
        registrationHolder = docRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            if (error != null) {
                registrationHolder.remove();
                onFailure.accept(error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                registrationHolder.remove();
                onSuccess.run();
            }
        });

        docRef.set(mood)
                .addOnFailureListener(e -> {
                    registrationHolder.remove();
                    onFailure.accept(e);
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
        docRef = db.collection("moods").document(moodId);
        registrationHolder = docRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            if (error != null) {
                registrationHolder.remove();
                onFailure.accept(error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                registrationHolder.remove();
                onSuccess.run();
            }
        });

        docRef.delete()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            onFailure.accept(e);
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            onFailure.accept(e);
                        }
                    }
                });
    }

    /**
     * Fetches all public moods for a specific user, then processes them on the device
     * to return only the top 3 most recent moods.
     *
     * @param username  The username to filter moods.
     * @param onSuccess Callback for returning a list of top 3 public moods.
     * @param onFailure Callback for handling errors.
     */
    public void getPublicMoodsByUser(String username,
                                     Consumer<List<Mood>> onSuccess,
                                     Consumer<Exception> onFailure) {
        db.collection("moods")
                .whereEqualTo("username", username)
                .whereEqualTo("public", true)  // Only public moods
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Mood> moods = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Mood mood = document.toObject(Mood.class);
                        mood.setDocId(document.getId());
                        moods.add(mood);
                    }
                    // Process moods on device: sort in descending order (most recent first)
                    // and limit to the top 3.
                    Collections.sort(moods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
                    List<Mood> topMoods = moods.size() > 3 ? moods.subList(0, 3) : moods;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onSuccess.accept(topMoods);
                    }
                })
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }

    /**
     * Fetches all moods for a specific user.
     *
     * @param username  The username to filter moods.
     * @param onSuccess Callback for returning a list of moods.
     * @param onFailure Callback for handling errors.
     */
    public void getMoodsByUser(String username,
                               Consumer<List<Mood>> onSuccess,
                               Consumer<Exception> onFailure) {
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
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }

    /**
     * Fetches all moods from the Firestore "moods" collection for testing purposes.
     *
     * @param onSuccess Callback that returns a list of Mood objects.
     * @param onFailure Callback for handling errors.
     */
    public void getAllMoods(Consumer<List<Mood>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("moods")
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
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }
}

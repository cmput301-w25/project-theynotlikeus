package com.example.theynotlikeus.controller;

import android.os.Build;

import androidx.annotation.NonNull;

import com.example.theynotlikeus.model.Comment;
import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class manages Firebase Firestore operations related to Comment objects.
 * It allows the addition of a new comment with real-time success detection
 * and fetching all comments associated with a specific mood ID
 */

public class CommentController extends FirebaseController {
    private final FirebaseFirestore db;

    //Listener holder for live snapshot updates during addComment()
    private ListenerRegistration registrationHolder = null;

    //Document reference to the specific comment being added
    private DocumentReference docRef;

    /**
     * Initializes the controller by getting the Firestore instance from FirebaseController.
     */
    public CommentController() {
        super();
        this.db = super.getFirebase();
    }

    /**
     * Adds a comment to the "comments" collection and listens for confirmation.
     *
     * @param comment    The Comment object to be added.
     * @param onSuccess  Callback to run once the comment is successfully added.
     * @param onFailure  Callback to run if the operation fails.
     */
    public void addComment(Comment comment, Runnable onSuccess, Consumer<Exception> onFailure) {
        docRef = db.collection("comments").document();

        //Listen for snapshot updates to confirm comment creation
        registrationHolder = docRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            if (error != null) {
                registrationHolder.remove();
                onFailure.accept(error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                if (registrationHolder != null) {
                    registrationHolder.remove();  //Stop listening once confirmed
                }
                onSuccess.run();  //Notify the UI
            }
        });

        //Actually write the comment to Firestore
        docRef.set(comment).addOnFailureListener(e -> {
            if (registrationHolder != null) {
                registrationHolder.remove();  //Clean up on failure
            }
        });
    }

    /**
     * Fetches all comments associated with a specific Mood ID.
     *
     * @param moodID     The ID of the mood to retrieve comments for.
     * @param onSuccess  Callback invoked with the list of comments on success.
     * @param onFailure  Callback invoked with an exception on failure.
     */
    public void getCommentsByMoodID(String moodID, Consumer<List<Comment>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("comments")
                .whereEqualTo("associatedMoodID", moodID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);

                        //Set the document ID as the comment's associatedMoodID
                        comment.setAssociatedMoodID(document.getId());
                        comments.add(comment);
                    }

                    //Only call back if running on supported Android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onSuccess.accept(comments);
                    }
                })
                .addOnFailureListener(e -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onFailure.accept(e);
                    }
                });
    }
}

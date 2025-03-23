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

public class CommentController extends FirebaseController {
    private final FirebaseFirestore db;
    private ListenerRegistration registrationHolder = null;
    private DocumentReference docRef;
    public CommentController() {
        super();
        this.db = super.getFirebase();
    }

    public void addComment(Comment comment, Runnable onSuccess, Consumer<Exception> onFailure) {
        docRef = db.collection("comments").document();
        registrationHolder = docRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            if (error != null) {
                registrationHolder.remove();
                onFailure.accept(error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                if (registrationHolder != null) {
                    registrationHolder.remove();
                }
                onSuccess.run();
            }
        });

        docRef.set(comment).addOnFailureListener(e -> {
            if (registrationHolder != null) {
                registrationHolder.remove();
            }
        });
    }

    public void getCommentsByMoodID(String moodID, Consumer<List<Comment>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("comments")
                .whereEqualTo("associatedMoodID", moodID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);
                        comment.setAssociatedMoodID(document.getId());
                        comments.add(comment);
                    }
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

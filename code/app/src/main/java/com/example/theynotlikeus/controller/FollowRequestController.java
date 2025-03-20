package com.example.theynotlikeus.controller;

import com.example.theynotlikeus.model.Request;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class FollowRequestController extends FirebaseController {

    private final FirebaseFirestore db;

    public FollowRequestController() {
        super();
        this.db = super.getFirebase();
    }

    /**
     * Accepts a follow request.
     *
     * This method creates a new document in the "follow" collection with the same
     * follower and followee values from the Request object and then deletes the corresponding
     * document from the "request" collection.
     *
     * @param request   The follow request to accept.
     * @param onSuccess Callback executed when both operations succeed.
     * @param onFailure Callback executed if any operation fails.
     */
    public void acceptRequest(Request request, Runnable onSuccess, OnFailureListener onFailure) {
        // Create a new Request object to serve as the "follow" document.
        Request follow = new Request();
        follow.setFollower(request.getFollower());
        follow.setFollowee(request.getFollowee());

        // Add the new document to the "follow" collection.
        db.collection("follow")
                .add(follow)
                .addOnSuccessListener(documentReference -> {
                    // After the follow document is successfully created, delete the request document.
                    if (request.getId() != null && !request.getId().isEmpty()) {
                        db.collection("request").document(request.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> onSuccess.run())
                                .addOnFailureListener(onFailure);
                    } else {
                        // If no valid ID is available, just invoke the success callback.
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Declines a follow request.
     *
     * This method deletes the corresponding document from the "request" collection.
     *
     * @param request   The follow request to decline.
     * @param onSuccess Callback executed when the deletion succeeds.
     * @param onFailure Callback executed if the deletion fails.
     */
    public void declineRequest(Request request, Runnable onSuccess, OnFailureListener onFailure) {
        if (request.getId() != null && !request.getId().isEmpty()) {
            db.collection("request").document(request.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> onSuccess.run())
                    .addOnFailureListener(onFailure);
        } else {
            onFailure.onFailure(new Exception("Invalid request id"));
        }
    }
}

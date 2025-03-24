package com.example.theynotlikeus.controller;

import com.example.theynotlikeus.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Controller handles CRUD operations for user objects in the Firestore database.
 */
public class UserController extends FirebaseController {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UserController() {
        // Initialize Firestore and Firebase Authentication
        super();
        this.db = super.getFirebase();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Method to handle user login
     */
    public void loginUser(String username, String password, final LoginCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onError("Incorrect username or password");
                    } else {
                        // Loop through query result to find user
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            User user = document.toObject(User.class);  // Convert to User object

                            if (user.getPassword().equals(password)) {
                                callback.onSuccess(user);
                            } else {
                                callback.onError("Incorrect username or password");
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error: " + e.getMessage()));
    }

    /**
     * Method to handle user sign-up
     */
    public void signUpUser(String username, String password, final SignUpCallback callback) {
        // Check if username already exists
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // User already exists
                        callback.onError("Username already exists");
                    } else {
                        // Create new user object
                        User newUser = new User(username, password);
                        // Save user to Firestore
                        db.collection("users")
                                .add(newUser)
                                .addOnSuccessListener(documentReference -> callback.onSuccess(newUser))
                                .addOnFailureListener(e -> callback.onError("Error creating account: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error checking username: " + e.getMessage()));
    }

    /**
     * Callback interface for login result
     */
    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    /**
     * Callback interface for sign-up result
     */
    public interface SignUpCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
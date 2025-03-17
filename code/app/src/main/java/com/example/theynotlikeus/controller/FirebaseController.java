package com.example.theynotlikeus.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheIndexManager;
import com.google.firebase.firestore.PersistentCacheSettings;

// FirebaseController
public class FirebaseController {
    /* Singleton pattern taken from: https://www.geeksforgeeks.org/singleton-class-java/
     * Authored by: GeeksForGeeks
     * Taken by: Ercel Angeles
     * Taken on: March 14, 2025
     */
    private FirebaseFirestore db;
    private static FirebaseController singleInstance;
    private static PersistentCacheIndexManager indexManager;
    private static FirebaseFirestoreSettings settings;

    /**
     * Constructor for the FirebaseController
     */
    public FirebaseController() {
        this.db = getFirebase();
    }

    /**
     * Gets the Firebase database.
     * @return this.db
     */
    public FirebaseFirestore getFirebase() {
        if (db == null) {
            // Obtain the Firestore instance.
            db = FirebaseFirestore.getInstance();

            // Build your settings without relying on db.getFirestoreSettings().
            settings = new FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build();

            // Immediately apply the settings before any other Firestore calls.
            db.setFirestoreSettings(settings);

            // Now it’s safe to get the PersistentCacheIndexManager from the same instance.
            indexManager = db.getPersistentCacheIndexManager();
            if (indexManager != null) {
                // Enable auto-indexing.
                indexManager.enableIndexAutoCreation();
            }
        }
        return db;
    }

    /**
     * Get a single instance of the controller
     * @return singleInstance
     */
    /*  Synchronization for singleton pattern: https://stackoverflow.com/questions/11165852/java-singleton-and-synchronization
     * Authored by: Jeffrey
     * Taken by: Ercel Angeles
     * Taken on: March 14, 2025
     */
    public static synchronized FirebaseController getInstance() {
        if (singleInstance == null) {
            singleInstance = new FirebaseController();
        }
        return singleInstance;
    }

}

package com.example.theynotlikeus.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheIndexManager;

public class FirebaseController {
    // Static singleton instance of Firestore.
    private static FirebaseFirestore db;
    // Singleton instance of this controller.
    private static FirebaseController singleInstance;
    // Cached Firestore settings.
    private static FirebaseFirestoreSettings settings;
    // Optional: Cache index manager.
    private static PersistentCacheIndexManager indexManager;

    // Private constructor to prevent direct instantiation.
    FirebaseController() {
        // Ensure Firestore is initialized.
        getFirebase();
    }

    /**
     * Returns the singleton instance of FirebaseController.
     */
    public static synchronized FirebaseController getInstance() {
        if (singleInstance == null) {
            singleInstance = new FirebaseController();
        }
        return singleInstance;
    }

    /**
     * Returns the singleton instance of FirebaseFirestore. Settings are configured
     * only once—before any Firestore operations occur.
     */
    public static synchronized FirebaseFirestore getFirebase() {
        if (db == null) {
            // Obtain the Firestore instance.
            db = FirebaseFirestore.getInstance();

            // Configure Firestore settings only once.
            settings = new FirebaseFirestoreSettings.Builder()
                    // Enable local persistence.
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);

            // Now that the settings have been applied, get the cache index manager.
            indexManager = db.getPersistentCacheIndexManager();
            if (indexManager != null) {
                indexManager.enableIndexAutoCreation();
            }
        }
        return db;
    }
}

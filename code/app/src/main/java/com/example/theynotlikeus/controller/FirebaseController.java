package com.example.theynotlikeus.controller;

import com.google.firebase.firestore.FirebaseFirestore;


// FirebaseController
public class FirebaseController {
    /* Singleton pattern taken from: https://www.geeksforgeeks.org/singleton-class-java/
     * Authored by: GeeksForGeeks
     * Taken by: Ercel Angeles
     * Taken on: March 14, 2025
     */
    private FirebaseFirestore db;
    private static FirebaseController singleInstance = null;

    /**
     * Constructor for the FirebaseController
     */
    public FirebaseController() {
        db = getFirebase();
    }

    /**
     * Gets the Firebase database.
     * @return this.db
     */
    public FirebaseFirestore getFirebase() {
        if (db == null) {
            this.db = FirebaseFirestore.getInstance();
        }
        return this.db;
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

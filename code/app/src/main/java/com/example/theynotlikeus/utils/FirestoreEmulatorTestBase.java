package com.example.theynotlikeus;

import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.BeforeClass;

public abstract class FirestoreEmulatorTestBase {
    private static boolean isEmulatorConfigured = false;

    @BeforeClass
    public static void configureFirestoreEmulator() {
        if (!isEmulatorConfigured) {
            try {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                // Replace with your emulator host and port (commonly "10.0.2.2" for Android emulators)
                firestore.useEmulator("10.0.2.2", 8080);
                isEmulatorConfigured = true;
            } catch (IllegalStateException e) {
                // The emulator is already configured; ignore the exception.
                isEmulatorConfigured = true;
            }
        }
    }
}

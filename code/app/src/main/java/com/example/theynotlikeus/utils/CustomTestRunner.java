package com.example.theynotlikeus.utils;

import android.os.Bundle;
import androidx.test.runner.AndroidJUnitRunner;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomTestRunner extends AndroidJUnitRunner {
    @Override
    public void onCreate(Bundle arguments) {
        // Configure the Firestore emulator before any instance is created.
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8089);
        super.onCreate(arguments);
    }
}
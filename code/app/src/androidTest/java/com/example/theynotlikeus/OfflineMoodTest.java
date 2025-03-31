package com.example.theynotlikeus;

import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test case to verify that mood events can be added and edited while offline,
 * and then correctly synchronized when the device goes back online.
 */
@RunWith(AndroidJUnit4.class)
public class OfflineMoodTest {

    private FirebaseFirestore db;
    private MoodController moodController;

    @BeforeClass
    public static void setupEmulator() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Clears the "moods" collection before each test to start with a clean slate.
     */
    @Before
    public void clearMoodsCollection() throws InterruptedException {
        db = FirebaseFirestore.getInstance();

        // Ensure offline persistence is enabled
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        CountDownLatch latch = new CountDownLatch(1);
        db.collection("moods")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        CountDownLatch deleteLatch = new CountDownLatch(querySnapshot.size());
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete().addOnCompleteListener(task -> deleteLatch.countDown());
                        }
                        try {
                            deleteLatch.await(5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Log.e("OfflineMoodTest", "Delete interrupted", e);
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("OfflineMoodTest", "Failed to clear moods collection", e);
                    latch.countDown();
                });
        latch.await(5, TimeUnit.SECONDS);

        // Initialize MoodController after clearing the collection.
        moodController = new MoodController();
    }

    /**
     * Test case to verify that a mood added while offline is synchronized when back online.
     */
    @Test
    public void testAddMoodOfflineThenSync() throws InterruptedException {
        // Simulate offline mode by disabling Firestore network.
        CountDownLatch disableLatch = new CountDownLatch(1);
        db.disableNetwork().addOnCompleteListener(task -> disableLatch.countDown());
        disableLatch.await(5, TimeUnit.SECONDS);

        // Create a unique mood event.
        String moodDocId = "testOfflineAdd_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("offlineUser");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("Offline mood event");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        // Add the mood while offline.
        CountDownLatch addLatch = new CountDownLatch(1);
        moodController.addMood(testMood, addLatch::countDown, e -> {
            Log.e("OfflineMoodTest", "Failed to add mood offline", e);
            addLatch.countDown();
        });
        addLatch.await(5, TimeUnit.SECONDS);

        // Re-enable network to simulate going back online.
        CountDownLatch enableLatch = new CountDownLatch(1);
        db.enableNetwork().addOnCompleteListener(task -> enableLatch.countDown());
        enableLatch.await(5, TimeUnit.SECONDS);

        // Give some time for the local changes to sync.
        Thread.sleep(5000);

        // Query Firestore to verify that the mood has been uploaded.
        CountDownLatch queryLatch = new CountDownLatch(1);
        final boolean[] found = {false};
        db.collection("moods").whereEqualTo("docId", moodDocId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        found[0] = true;
                    }
                    queryLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("OfflineMoodTest", "Failed to query Firestore", e);
                    queryLatch.countDown();
                });
        queryLatch.await(5, TimeUnit.SECONDS);
        assertTrue("Offline added mood should be synced when online", found[0]);
    }
}

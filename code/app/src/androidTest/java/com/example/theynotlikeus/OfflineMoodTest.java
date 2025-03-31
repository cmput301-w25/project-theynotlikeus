package com.example.theynotlikeus;

import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
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

    @Before
    public void clearMoodsCollection() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
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
                            // Handle interruption if needed.
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);

        // Initialize MoodController after clearing the collection.
        moodController = new MoodController();
    }

    @Test
    public void testAddMoodOfflineThenSync() throws InterruptedException {
        CountDownLatch disableLatch = new CountDownLatch(1);
        db.disableNetwork().addOnCompleteListener(task -> disableLatch.countDown());
        disableLatch.await(5, TimeUnit.SECONDS);

        String moodDocId = "testOfflineAdd_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("offlineUser");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("Offline mood event");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        CountDownLatch addLatch = new CountDownLatch(1);
        moodController.addMood(testMood, addLatch::countDown, e -> addLatch.countDown());
        addLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch enableLatch = new CountDownLatch(1);
        db.enableNetwork().addOnCompleteListener(task -> enableLatch.countDown());
        enableLatch.await(5, TimeUnit.SECONDS);

        Thread.sleep(5000);

        CountDownLatch queryLatch = new CountDownLatch(1);
        final boolean[] found = {false};
        db.collection("moods").whereEqualTo("docId", moodDocId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                found[0] = true;
            }
            queryLatch.countDown();
        });
        queryLatch.await(5, TimeUnit.SECONDS);
        assertTrue("Offline added mood should be synced when online", found[0]);
    }

    @Test
    public void testEditMoodOfflineThenSync() throws InterruptedException {
        String moodDocId = "testOfflineEdit_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("offlineEditUser");
        testMood.setMoodState(Mood.MoodState.SADNESS);
        testMood.setTrigger("Initial offline edit event");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        CountDownLatch addLatch = new CountDownLatch(1);
        moodController.addMood(testMood, addLatch::countDown, e -> addLatch.countDown());
        addLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch disableLatch = new CountDownLatch(1);
        db.disableNetwork().addOnCompleteListener(task -> disableLatch.countDown());
        disableLatch.await(5, TimeUnit.SECONDS);

        testMood.setTrigger("Updated offline mood event");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);

        CountDownLatch updateLatch = new CountDownLatch(1);
        moodController.updateMood(testMood, updateLatch::countDown, e -> updateLatch.countDown());
        updateLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch enableLatch = new CountDownLatch(1);
        db.enableNetwork().addOnCompleteListener(task -> enableLatch.countDown());
        enableLatch.await(5, TimeUnit.SECONDS);

        final long timeoutMillis = 15000;
        final long pollingInterval = 1000;
        long startTime = System.currentTimeMillis();
        Mood updatedMood = null;
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            CountDownLatch queryLatch = new CountDownLatch(1);
            final Mood[] queryResult = new Mood[1];
            db.collection("moods").whereEqualTo("docId", moodDocId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                    queryResult[0] = task.getResult().getDocuments().get(0).toObject(Mood.class);
                }
                queryLatch.countDown();
            });
            queryLatch.await(5, TimeUnit.SECONDS);
            if (queryResult[0] != null &&
                    "Updated offline mood event".equals(queryResult[0].getTrigger()) &&
                    queryResult[0].getMoodState() == Mood.MoodState.HAPPINESS) {
                updatedMood = queryResult[0];
                break;
            }
            Thread.sleep(pollingInterval);
        }
        assertTrue("Offline updated mood should be synced when online", updatedMood != null);
    }
}

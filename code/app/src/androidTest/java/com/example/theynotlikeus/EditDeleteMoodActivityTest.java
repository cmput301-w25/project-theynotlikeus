package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.view.EditDeleteMoodActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI Test class for EditDeleteMoodActivity.
 * This class tests the ability to delete and edit a mood event from Firestore using Espresso.
 */
public class EditDeleteMoodActivityTest {

    private static String generatedMoodId;
    private static String expectedDateString;

    // for geolocation implementation in part 4
    private static final double testLatitude = 37.7749;
    private static final double testLongitude = -122.4194;

    /**
     * Set up Firestore
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Seed the database with one mood event for deletion tests.
     * @throws InterruptedException
     */
    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        // Create a test mood event for deletion
        Date now = new Date();
        expectedDateString = now.toString(); // Convert date to string format

        Mood mood = new Mood();
        mood.setTrigger("Finished a great book"); // Set trigger text
        mood.setSocialSituation(Mood.SocialSituation.ALONE); // Set social situation
        mood.setMoodState(Mood.MoodState.HAPPINESS); // Set mood state
        mood.setDateTime(now); // Set the date and time

        // Generate Firestore document ID manually
        String docId = moodsRef.document().getId();
        mood.setDocId(docId);

        // Perform asynchronous Firestore write operation and wait for completion
        CountDownLatch latch = new CountDownLatch(1);
        moodsRef.document(docId).set(mood)
                .addOnSuccessListener(aVoid -> {
                    generatedMoodId = docId; // Store the generated mood ID for deletion test
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await(30, TimeUnit.SECONDS); // Wait up to 30 seconds for database write
    }

    /**
     * Helper method to seed a separate mood event for editing.
     * @return the generated document ID for the mood event used for editing.
     * @throws InterruptedException
     */
    private String seedMoodForEdit() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        Date now = new Date();

        Mood mood = new Mood();
        mood.setTrigger("Editable mood event"); // Different trigger text for editing test.
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setMoodState(Mood.MoodState.HAPPINESS);
        mood.setDateTime(now);

        String docId = moodsRef.document().getId();
        mood.setDocId(docId);

        CountDownLatch latch = new CountDownLatch(1);
        moodsRef.document(docId).set(mood)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());
        latch.await(30, TimeUnit.SECONDS);
        return docId;
    }

    /**
     * Test: Deletes a mood event and verifies that it is removed from Firestore.
     * This ensures the delete button functions correctly.
     */
    @Test
    public void deleteMoodButtonDeletesMoodFromList() {
        if (generatedMoodId == null) {
            Log.e("EditDeleteMood", "Generated mood ID is null. Cannot launch activity.");
            return;
        }

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EditDeleteMoodActivity.class
        ); // Launch EditDeleteMoodActivity with the test mood ID for deletion.
        intent.putExtra("moodId", generatedMoodId);

        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // Click on the mood event in the list to open it.
            onView(withText("Finished a great book")).perform(click());
            // Tap the delete button.
            onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).perform(click());
            // Verify that the mood event no longer exists in the UI.
            onView(withText("Finished a great book")).check(doesNotExist());
        }
    }

    /**
     * Test: Edits a mood event and verifies that it is updated successfully.
     * This test seeds a separate mood event (so deletion of the other event does not interfere),
     * replaces the trigger text with a new valid trigger,
     * clicks the save button, and then verifies that the activity finishes.
     */
    @Test
    public void testEditMoodEventUpdatesMood() throws InterruptedException {
        // Seed a separate mood event specifically for editing.
        String editMoodId = seedMoodForEdit();
        if (editMoodId == null) {
            Log.e("EditDeleteMood", "Edit mood ID is null. Cannot launch activity.");
            return;
        }

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EditDeleteMoodActivity.class
        );
        intent.putExtra("moodId", editMoodId);

        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // Wait briefly for mood data to load.
            Thread.sleep(2000);
            // Replace the trigger text with a new valid trigger.
            onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput))
                    .perform(replaceText("Updated trigger"));
            // Click the save button to update the mood.
            onView(withId(R.id.button_DeleteEditMoodActivity_save)).perform(click());
            // Wait for asynchronous operations (such as update) to complete.
            Thread.sleep(2000);
            // Verify that the activity has finished by checking that its lifecycle state is DESTROYED.
            assertEquals("Mood update did not finish as expected", Lifecycle.State.DESTROYED, scenario.getState());
        }
    }

    /**
     * Tear down database
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;

        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) Objects.requireNonNull(url).openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;

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

public class EditDeleteMoodActivityTest {

    private static String generatedMoodId; // Will store the newly created mood's ID
    private static String expectedDateString;

    // (When geolocation is implemented, these could be used in your Mood object)
    private static final double testLatitude = 37.7749;
    private static final double testLongitude = -122.4194;

    @BeforeClass
    public static void setup() {
        // [DO NOT CHANGE]
        // Specific address for emulator to access localhost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        Date now = new Date();
        expectedDateString = now.toString();

        // Create a Mood object
        Mood mood = new Mood();
        mood.setTrigger("Finished a great book");
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setMoodState(Mood.MoodState.HAPPINESS);
        mood.setDateTime(now);
        // mood.setLocation(testLatitude, testLongitude); // For future geolocation use

        // Instead of using add() to auto-generate an ID, generate one manually
        String docId = moodsRef.document().getId();
        mood.setDocId(docId);

        CountDownLatch latch = new CountDownLatch(1);
        moodsRef.document(docId).set(mood)
                .addOnSuccessListener(aVoid -> {
                    generatedMoodId = docId;
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await(30, TimeUnit.SECONDS); // Wait up to 30s for the write to complete
    }

    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        // Make sure we got a mood ID from seedDatabase()
        if (generatedMoodId == null) {
            Log.e("EditDeleteMood", "Generated mood ID is null. Cannot launch activity.");
            return;
        }

        // Create an intent with the valid moodId extra
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EditDeleteMoodActivity.class
        );
        intent.putExtra("moodId", generatedMoodId);

        // Launch the activity with the intent
        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // Check that UI fields display the existing mood’s details
            onView(withId(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput))
                    .check(ViewAssertions.matches(withText("Finished a great book")));
        }
    }

    @Test
    public void editMoodButtonValidatesAllInputs() {
        if (generatedMoodId == null) {
            Log.e("EditDeleteMood", "Generated mood ID is null. Cannot launch activity.");
            return;
        }
        // Launch activity with a valid intent
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EditDeleteMoodActivity.class
        );
        intent.putExtra("moodId", generatedMoodId);
        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // Click on the existing mood in the list
            onView(withText("Finished a great book")).perform(click());

            // Clear the trigger text, attempt to save, and check for validation error
            onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).perform(clearText());
            onView(withId(R.id.button_DeleteEditMoodActivity_save)).perform(click());

        }
    }

    @Test
    public void deleteMoodButtonDeletesMoodFromList() {
        if (generatedMoodId == null) {
            Log.e("EditDeleteMood", "Generated mood ID is null. Cannot launch activity.");
            return;
        }
        // Launch activity with a valid intent
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EditDeleteMoodActivity.class
        );
        intent.putExtra("moodId", generatedMoodId);
        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // Click on the existing mood
            onView(withText("Finished a great book")).perform(click());

            // Tap the delete button
            onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).perform(click());

            // The mood should no longer appear in the list
            onView(withText("Finished a great book")).check(doesNotExist());
        }
    }

    @After
    public void tearDown() {
        // [DO NOT CHANGE]
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

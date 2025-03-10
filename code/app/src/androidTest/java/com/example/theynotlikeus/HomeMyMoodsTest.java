package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeMyMoodsTest {

    /**
     * Setup Firestore to use the local emulator and pre-populate it with a mood event
     * for the test user "testUser".
     */
    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

        // Pre-populate Firestore with a test mood event.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mood = new HashMap<>();
        mood.put("username", "testUser");
        mood.put("dateTime", new Timestamp(new Date()));
        // The fragment expects moodState in uppercase.
        mood.put("moodState", "HAPPINESS");
        mood.put("trigger", "happy trigger");
        // Using a fixed document id for testing.
        db.collection("moods").document("testMoodDoc").set(mood);
    }

    /**
     * Test that when HomeActivity (which hosts HomeMyMoodsFrag) is launched with the username
     * extra, the welcome message and the pre-populated mood event are displayed.
     */
    @Test
    public void testDisplayMoodsAndWelcomeMessage() throws InterruptedException {
        // Launch HomeActivity with an intent extra "username" set to "testUser".
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testUser");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            // Wait for Firestore to load the mood data.
            Thread.sleep(3000);

            // Verify the welcome message.
            onView(withId(R.id.textView_HomeMyMoodsFragment_welcomeUser))
                    .check(matches(withText("Welcome, testUser!")));

            // Verify that the mood event's trigger text ("happy trigger") is displayed.
            onView(withText("happy trigger"))
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Test that clicking on the "add mood" FloatingActionButton navigates to the AddMoodEventActivity.
     *
     * This test assumes that the AddMoodEventActivity's layout contains a view with the id "add_mood_event_layout".
     */
    @Test
    public void testAddMoodButtonNavigation() throws InterruptedException {
        // Launch MainActivity with an intent extra "username" set to "testUser".
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testUser");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            // Wait for the Firestore data to load.
            Thread.sleep(3000);

            // Click the FloatingActionButton to add a new mood event.
            onView(withId(R.id.floatingActionButton_HomeMyMoodsFragment_addmood))
                    .perform(click());

            // Verify that AddMoodEventActivity is displayed by checking for the Save button.
            onView(withId(R.id.button_ActivityAddMoodEvent_save))
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Clean up the Firestore emulator by deleting all documents in the "moods" collection.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1"; // Update this if your project id is different.
        try {
            URL url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId +
                    "/databases/(default)/documents/moods");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            int responseCode = connection.getResponseCode();
            Log.i("Firestore Cleanup", "Response code: " + responseCode);
            connection.disconnect();
        } catch (Exception e) {
            Log.e("Firestore Cleanup", Objects.requireNonNull(e.getMessage()));
        }
    }
}

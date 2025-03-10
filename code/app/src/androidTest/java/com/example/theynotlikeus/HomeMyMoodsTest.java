package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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

import org.junit.AfterClass;
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
     * Pre-populate Firestore with multiple mood events for testUser.
     * For each mood, two events are added:
     *   - A recent event (current timestamp)
     *   - An old event (timestamp older than 7 days)
     */
    @BeforeClass
    public static void populateFirestore() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator(androidLocalhost, portNumber);

        String username = "testUser";
        long now = System.currentTimeMillis();
        long eightDaysAgo = now - (8L * 24 * 60 * 60 * 1000);

        String[] moodStates = {"HAPPINESS", "SADNESS", "ANGER", "SURPRISE", "FEAR", "DISGUST", "SHAME"};
        for (String moodState : moodStates) {
            // Create a recent event.
            Map<String, Object> recentEvent = new HashMap<>();
            recentEvent.put("username", username);
            recentEvent.put("dateTime", new Timestamp(new Date(now)));
            recentEvent.put("moodState", moodState);
            recentEvent.put("trigger", "recent " + moodState);
            firestore.collection("moods").document("recent_" + moodState).set(recentEvent);

            // Create an old event.
            Map<String, Object> oldEvent = new HashMap<>();
            oldEvent.put("username", username);
            oldEvent.put("dateTime", new Timestamp(new Date(eightDaysAgo)));
            oldEvent.put("moodState", moodState);
            oldEvent.put("trigger", "old " + moodState);
            firestore.collection("moods").document("old_" + moodState).set(oldEvent);
        }
    }

    /**
     * Test that MainActivity (hosting HomeMyMoodsFrag) displays the welcome message and all pre-populated mood events.
     */
    @Test
    public void testDisplayAllMoods() throws InterruptedException {
        // Launch MainActivity with the test user's username.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testUser");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            // Wait for Firestore to load the mood events.
            Thread.sleep(5000);

            // Verify the welcome message.
            onView(withId(R.id.textView_HomeMyMoodsFragment_welcomeUser))
                    .check(matches(withText("Welcome, testUser!")));

            // Verify that each mood's recent and old events are displayed.
            String[] moodStates = {"HAPPINESS", "SADNESS", "ANGER", "SURPRISE", "FEAR", "DISGUST", "SHAME"};
            for (String moodState : moodStates) {
                onView(withText("recent " + moodState)).check(matches(isDisplayed()));
                onView(withText("old " + moodState)).check(matches(isDisplayed()));
            }
        }
    }

    /**
     * Test that clicking the "Recent Week" checkbox filters the events, so that only recent events are displayed.
     */
    @Test
    public void testRecentWeekFilter() throws InterruptedException {
        // Launch MainActivity with the test user's username.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testUser");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            // Wait for data to load.
            Thread.sleep(5000);

            // Click the "Recent Week" checkbox to filter events.
            onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).perform(click());
            Thread.sleep(2000);

            // Only recent events should be visible; old events should be absent.
            String[] moodStates = {"HAPPINESS", "SADNESS", "ANGER", "SURPRISE", "FEAR", "DISGUST", "SHAME"};
            for (String moodState : moodStates) {
                onView(withText("recent " + moodState)).check(matches(isDisplayed()));
                onView(withText("old " + moodState)).check(doesNotExist());
            }
        }
    }

    /**
     * Clean up the Firestore emulator by deleting all documents in the "moods" collection after all tests.
     */
    @AfterClass
    public static void cleanUpFirestore() {
        String projectId = "theynotlikeus-6a9f1"; // Update this if your project ID is different.
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

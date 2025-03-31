package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UI test for HomeMapFrag.
 *
 * This test assumes that the Firestore emulator is configured via CustomTestRunner.
 * It inserts a test mood event for "defaultUser" with valid geo coordinates (so that a marker should appear on the map),
 * launches MainActivity, clicks on the bottom navigation item for the map,
 * and verifies that the map container view (with id "mapUserFragment") is displayed.
 */
@RunWith(AndroidJUnit4.class)
public class HomeMapFragTest {

    @BeforeClass
    public static void setup() throws InterruptedException {
        // Remove duplicate emulator configuration; it's already handled by CustomTestRunner.
        // FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8089);

        // Insert a test mood event for "defaultUser" with valid geo coordinates.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mood = new HashMap<>();
        mood.put("username", "defaultUser");
        mood.put("moodState", "HAPPINESS");
        mood.put("dateTime", new Date());
        mood.put("latitude", 37.422);
        mood.put("longitude", -122.084);
        mood.put("pendingReview", false);
        // Write the mood to the "moods" collection with a fixed document ID.
        db.collection("moods").document("test_mood_event").set(mood);

        // Wait a few seconds to ensure the document is written.
        SystemClock.sleep(3000);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMapFragmentIsDisplayed() throws InterruptedException {
        // Launch MainActivity.
        ActivityScenario.launch(MainActivity.class);

        // Simulate clicking on the bottom navigation item for the map.
        onView(withId(R.id.nav_map)).perform(click());

        // Wait for HomeMapFrag to load its data and display the map.
        SystemClock.sleep(5000);

        // Verify that the map container view (with id "mapUserFragment") is displayed.
        onView(withId(R.id.mapUserFragment)).check(matches(isDisplayed()));
    }
}

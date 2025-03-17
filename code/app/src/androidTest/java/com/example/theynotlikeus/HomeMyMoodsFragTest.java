package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;
import com.example.theynotlikeus.view.RecyclerViewItemCountAssertion;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UI test for HomeMyMoodsFrag
 */

@RunWith(AndroidJUnit4.class)
public class HomeMyMoodsFragTest {

    /**
     * Set up Firestore
     */
    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        // "10.0.2.2" is used to access localhost from an Android emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Set scenario to be MainActivity
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testHomeMyMoodsFragmentIsDisplayedByDefault() {
        // Verify that the HomeMyMoodsFragment is displayed by default upon launching MainActivity
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));
    }

    /**
     * Helper method to add 10 mood events with different mood states to the local Firestore database.
     */
    private void addMoodsToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Array of mood states to cycle through.
        String[] moodStates = {
                "ANGER", "CONFUSION", "DISGUST", "FEAR", "HAPPINESS",
                "SADNESS", "SHAME", "SURPRISE", "BOREDOM", "HAPPINESS" // repeated for 10 items
        };

        for (int i = 0; i < 10; i++) {
            Map<String, Object> mood = new HashMap<>();
            mood.put("title", "Mood " + (i + 1));
            mood.put("description", "Description for Mood " + (i + 1));
            // Use "dateTime" to match the query in your fragment.
            mood.put("dateTime", new Date());
            // Use "defaultUser" to match the username in your fragment.
            mood.put("username", "defaultUser");
            // Set the mood state from our array.
            mood.put("moodState", moodStates[i]);
            // Optionally add a trigger for further filtering.
            mood.put("trigger", "trigger" + (i + 1));

            // Write the mood data to the "moods" collection with document id "mood_(i+1)"
            db.collection("moods").document("mood_" + (i + 1)).set(mood);
        }
    }

    /**
     * Test: moods are displayed
     * @throws InterruptedException
     */
    @Test
    public void testMoodsAreDisplayed() throws InterruptedException {
        // Populate the local database with 10 mood events.
        addMoodsToDatabase();

        // Wait to ensure data is written.
        Thread.sleep(3000);

        // Now launch MainActivity.
        ActivityScenario.launch(MainActivity.class);

        // Wait for the fragment to load the data.
        Thread.sleep(3000);

        // Verify that the RecyclerView is displayed.
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));

        // Verify that exactly 10 items are present in the RecyclerView.
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(new RecyclerViewItemCountAssertion(10));
    }
}

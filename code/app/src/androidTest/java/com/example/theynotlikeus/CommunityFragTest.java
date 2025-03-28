package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI test for CommunityFrag.
 */

@RunWith(AndroidJUnit4.class)
public class CommunityFragTest {

    @BeforeClass
    public static void setupFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8089);

        String[] followees = {"friend1", "friend2"};
        String[] moodStates = {"HAPPINESS", "SADNESS", "ANGER"};
        String[] triggers = {"happy event", "sad day", "angry moment"};

        Runnable onComplete = () -> System.out.println("Firestore setup complete!");

        // Keep track of Firestore writes and wait for them to complete
        int totalWrites = followees.length + (followees.length * 5); // 2 followees + 5 moods each
        final int[] completedWrites = {0};

        // Add test followees
        for (String followee : followees) {
            Map<String, Object> followData = new HashMap<>();
            followData.put("follower", "testUser");
            followData.put("followee", followee);
            db.collection("follow").document(followee).set(followData)
                    .addOnSuccessListener(aVoid -> {
                        completedWrites[0]++;
                        if (completedWrites[0] == totalWrites) {
                            onComplete.run();
                        }
                    });
        }

        // Add moods for each followee
        for (String followee : followees) {
            for (int i = 0; i < 5; i++) {
                Map<String, Object> mood = new HashMap<>();
                mood.put("username", followee);
                mood.put("moodState", moodStates[i % moodStates.length]);
                mood.put("trigger", triggers[i % triggers.length]);
                mood.put("dateTime", new Date(System.currentTimeMillis() - (i * 1000000)));
                mood.put("isPublic", true);
                db.collection("moods").add(mood)
                        .addOnSuccessListener(aVoid -> {
                            completedWrites[0]++;
                            if (completedWrites[0] == totalWrites) {
                                onComplete.run();
                            }
                        });
            }
        }
    }


    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCommunityMoodFiltering() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Wait until Firestore data is confirmed
        db.collection("moods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Firestore data is ready, starting test...");

                // Launch MainActivity and navigate to CommunityFrag
                ActivityScenario.launch(MainActivity.class);
                onView(withId(R.id.nav_community)).perform(click());

                // Verify that CommunityFrag RecyclerView is displayed
                onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));

                // Verify that each followee only shows 3 moods (before filtering)
                onView(withId(R.id.recyclerview_CommunityFrag_users)).check(new RecyclerViewItemCountAssertion(6)); // 3 moods per 2 users

                // Apply "Recent Week" filter
                onView(withId(R.id.checkBox_CommunityFrag_recentWeek)).perform(click());
                onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));

                // Apply Emotional State Filter (Select "HAPPINESS")
                onView(withId(R.id.community_autoCompleteTextView)).perform(click());
                onView(withText("Happiness")).perform(click());
                onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));

                // Apply Trigger Text Filter ("happy event")
                onView(withId(R.id.community_search_edit_text)).perform(replaceText("happy event"));
                onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));

                // Test opening CommunityMapActivity
                onView(withId(R.id.nav_map)).perform(click());
                onView(withId(R.id.button_MapUserFrag_toCommunityMap)).perform(click());
                pressBack();
            } else {
                System.err.println("Firestore data fetch failed!");
            }
        });
    }

}

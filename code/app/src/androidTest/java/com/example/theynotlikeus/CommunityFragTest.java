package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class CommunityFragTest {

    @BeforeClass
    public static void setupFirestore() {
        // Remove the emulator configuration call since CustomTestRunner already sets it:
        // FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8089);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Test data: two followees, each with 5 moods.
        String[] followees = {"friend1", "friend2"};
        String[] moodStates = {"HAPPINESS", "SADNESS", "ANGER"};
        String[] triggers = {"happy event", "sad day", "angry moment"};

        // Total writes = number of followees + (followees * 5 moods)
        int totalWrites = followees.length + (followees.length * 5); // e.g., 2 + 10 = 12
        CountDownLatch latch = new CountDownLatch(totalWrites);

        // Add test followees.
        for (String followee : followees) {
            Map<String, Object> followData = new HashMap<>();
            followData.put("follower", "testUser");
            followData.put("followee", followee);
            db.collection("follow").document(followee).set(followData)
                    .addOnSuccessListener(aVoid -> latch.countDown())
                    .addOnFailureListener(e -> latch.countDown());
        }

        // Add moods for each followee.
        for (String followee : followees) {
            for (int i = 0; i < 5; i++) {
                Map<String, Object> mood = new HashMap<>();
                mood.put("username", followee);
                mood.put("moodState", moodStates[i % moodStates.length]);
                mood.put("trigger", triggers[i % triggers.length]);
                mood.put("dateTime", new Date(System.currentTimeMillis() - (i * 1000000)));
                mood.put("isPublic", true);
                db.collection("moods").add(mood)
                        .addOnSuccessListener(aVoid -> latch.countDown())
                        .addOnFailureListener(e -> latch.countDown());
            }
        }

        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                System.err.println("Timed out waiting for Firestore writes to complete.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCommunityThreeMoods() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean dataReady = new AtomicBoolean(false);

        db.collection("moods").get().addOnCompleteListener(task -> {
            dataReady.set(task.isSuccessful());
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS) || !dataReady.get()) {
            fail("Firestore data fetch failed!");
        }

        // Launch MainActivity and navigate to CommunityFrag.
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.nav_community)).perform(click());

        // Verify that the RecyclerView is displayed.
        onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));

        // Verify that each followee shows 3 moods (3 per 2 users = 6 items total).
        onView(withId(R.id.recyclerview_CommunityFrag_users)).check(new RecyclerViewItemCountAssertion(6));
    }

    @Test
    public void testCommunityRecentWeek() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean dataReady = new AtomicBoolean(false);

        db.collection("moods").get().addOnCompleteListener(task -> {
            dataReady.set(task.isSuccessful());
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS) || !dataReady.get()) {
            fail("Firestore data fetch failed!");
        }

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.nav_community)).perform(click());

        // Apply the "Recent Week" filter.
        onView(withId(R.id.checkBox_CommunityFrag_recentWeek)).perform(click());
        onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));
    }

    @Test
    public void testCommunityMoodFiltering() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean dataReady = new AtomicBoolean(false);

        db.collection("moods").get().addOnCompleteListener(task -> {
            dataReady.set(task.isSuccessful());
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS) || !dataReady.get()) {
            fail("Firestore data fetch failed!");
        }

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.nav_community)).perform(click());

        // Apply the emotional state filter (select "HAPPINESS").
        onView(withId(R.id.community_autoCompleteTextView)).perform(click());
        onView(withText("Happiness")).perform(click());
        onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));
    }

    @Test
    public void testCommunityReasonFilter() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean dataReady = new AtomicBoolean(false);

        db.collection("moods").get().addOnCompleteListener(task -> {
            dataReady.set(task.isSuccessful());
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS) || !dataReady.get()) {
            fail("Firestore data fetch failed!");
        }

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.nav_community)).perform(click());

        // Apply the trigger text filter ("happy event").
        onView(withId(R.id.community_search_edit_text)).perform(replaceText("happy event"));
        onView(withId(R.id.recyclerview_CommunityFrag_users)).check(matches(isDisplayed()));
    }

    @Test
    public void testCommunityMapButton() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean dataReady = new AtomicBoolean(false);

        db.collection("moods").get().addOnCompleteListener(task -> {
            dataReady.set(task.isSuccessful());
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS) || !dataReady.get()) {
            fail("Firestore data fetch failed!");
        }

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.nav_community)).perform(click());

        // Test opening the CommunityMapActivity.
        onView(withId(R.id.nav_map)).perform(click());
        onView(withId(R.id.button_MapUserFrag_toCommunityMap)).perform(click());
        onView(withId(R.id.fragment_CommunityMapActivity_map)).check(matches(isDisplayed()));
        pressBack();
    }
}

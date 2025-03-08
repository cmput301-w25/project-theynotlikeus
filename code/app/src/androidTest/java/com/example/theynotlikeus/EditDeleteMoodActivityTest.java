package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNull;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI + Firestore test for EditDeleteMoodActivity.
 */
@RunWith(AndroidJUnit4.class)
public class EditDeleteMoodActivityTest {

    private static final String TEST_MOOD_ID = "testMood123";
    private FirebaseFirestore db;
    private DocumentReference moodRef;
    private static String testDate;
    private static String testTime;
    private static double testLatitude = 1.02;
    private static double testLongitude = 8.18;
    @Rule
    public ActivityScenarioRule<EditDeleteMoodActivity> activityScenarioRule =
            new ActivityScenarioRule<>(new Intent()
                    .putExtra("moodId", TEST_MOOD_ID)
                    .putExtra("testMode", true)
                    .setClassName("com.example.theynotlikeus", "com.example.theynotlikeus.EditDeleteMoodActivity"));

    @BeforeClass
    public static void setup() {
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
    }

    @Before
    public void setUpFirestore() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        // Create a new mood
        Mood mood = new Mood();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        testDate = dateFormat.format(now);
        testTime = timeFormat.format(now);
        mood.setTrigger("Feeling great");
        mood.setMoodState(Mood.MoodState.HAPPINESS);
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setLocation(testLatitude, testLongitude);
        mood.setDateTime(now);
        moodsRef.add(mood);

//        moodsRef.document(TEST_MOOD_ID).set(mood, SetOptions.merge())
//                .addOnSuccessListener(aVoid -> latch.countDown());

        // Wait for Firestore
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

//        moodRef = db.collection("moods").document(TEST_MOOD_ID);
//        moodsRef.document(TEST_MOOD_ID).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult().exists()) {
//                Log.d("FirestoreDebug", "Mood Loaded: " + task.getResult().getData());
//            } else {
//                Log.e("FirestoreDebug", "Mood not found!");
//            }
//        });
        db.collection("moods").document(TEST_MOOD_ID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object triggerValue = documentSnapshot.get("trigger");
                        if (triggerValue == null) {
                            Log.e("FirestoreDebug", "ERROR: trigger is NULL");
                        } else {
                            Log.d("FirestoreDebug", "SUCCESS: trigger = " + triggerValue.toString());
                        }
                    } else {
                        Log.e("FirestoreDebug", "Mood document does not exist!");
                    }
                });
    }

    @Test
    public void testActivityLaunch() {
        // Check if save button is displayed
        onView(withId(R.id.button_DeleteEditMoodActivity_save)).check(matches(isDisplayed()));
    }


    @Test
    public void testEditAndSaveMood() {
        // Ensure mood is created and UI loads correctly
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput))
                .check(matches(isDisplayed()))
                .check(matches(withText("Feeling great")));

        // Update trigger text
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).perform(replaceText("Updated Mood"));

        // Click Save
        onView(withId(R.id.button_DeleteEditMoodActivity_save)).perform(click());

        // Verify update
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).check(matches(withText("Updated Mood")));
    }
    @Test
    public void testDeleteMood() throws InterruptedException {


        // Perform delete action
        onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).perform(click());


        CountDownLatch latch = new CountDownLatch(1);
        // Delete function
        moodRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                assertNull("Mood should be deleted from Firestore", snapshot.exists() ? snapshot : null);
            }
            latch.countDown();
        });
        // Wait for the database
        latch.await(3, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        if (moodRef != null) {
            moodRef.delete();
        }
    }
}

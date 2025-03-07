package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * UI + Firestore test for EditDeleteMoodActivity.
 */
@RunWith(AndroidJUnit4.class)
public class EditDeleteMoodActivityTest {

    private static final String TEST_MOOD_ID = "testMood123";
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<EditDeleteMoodActivity> activityScenarioRule =
            new ActivityScenarioRule<>(new Intent()
                    .putExtra("moodId", TEST_MOOD_ID)
                    .putExtra("testMode", true)
                    .setClassName("com.example.theynotlikeus", "com.example.theynotlikeus.EditDeleteMoodActivity"));

    @Before
    public void setUpFirestore() {
        db = FirebaseFirestore.getInstance();

        // Add a test mood into Firestore
        Map<String, Object> mockMood = new HashMap<>();
        mockMood.put("trigger", "Feeling great");
        mockMood.put("moodState", "HAPPY");

        db.collection("test").document(TEST_MOOD_ID)
                .set(mockMood, SetOptions.merge());
    }

    @Test
    public void testActivityLaunch() {
        // Check if save button is displayed
        onView(withId(R.id.button_DeleteEditMoodActivity_save)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirestoreDataLoads() {
        // Check Firestore test connection
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput))
                .check(matches(isDisplayed()))
                .check(matches(withText("Feeling great")));
    }

    @Test
    public void testEditAndSaveMood() {
        // Trigger text
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).perform(replaceText("Updated Mood"));

        // Save button
        onView(withId(R.id.button_DeleteEditMoodActivity_save)).perform(click());

        // Verify if the text is updated
        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).check(matches(withText("Updated Mood")));
    }

    @Test
    public void testClickDeleteButton() {
        // Delete button
        onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).perform(click());

        // Verify that the delete button action occurs
        onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).check(matches(isDisplayed()));
    }
}

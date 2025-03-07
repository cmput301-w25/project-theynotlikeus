package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test class for EditDeleteMoodActivity.
 */
public class EditDeleteMoodActivityUnitTest {

    private ActivityScenario<EditDeleteMoodActivity> scenario;

    @Before
    public void setUp() {
        // Create an Intent with a moodId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditDeleteMoodActivity.class);
        intent.putExtra("moodId", "testMood123");

        // Launch the activity
        scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void testIntentMoodId() {
        scenario.onActivity(activity -> {
            // Check if the intent contains the "moodId" key
            if (activity.getIntent().hasExtra("moodId")) {
                assertEquals("testMood123", activity.getIntent().getStringExtra("moodId"));
            }
        });
    }
}

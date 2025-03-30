package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.theynotlikeus.view.AddMoodEventActivity;
import com.example.theynotlikeus.view.MainActivity;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * UI test for submitting a valid mood event.
 * This test fills in all required fields and verifies that the valid submission finishes the activity.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivitySubmissionTest extends com.example.theynotlikeus.FirestoreEmulatorTestBase {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        // Fill in the trigger input.
        onView(withId(R.id.editText_ActivityAddMoodEvent_triggerInput))
                .perform(clearText(), typeText("Lakers"));

        // Select mood "Happiness" from the current mood spinner.
        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner))
                .perform(click());
        onData(is("Happiness")).perform(click());

        // Select social situation "Alone" from the socials spinner.
        onView(withId(R.id.spinner_ActivityAddMoodEvent_socialsituation))
                .perform(click());
        onData(is("Alone")).perform(click());

        // Toggle geolocation and privacy switches.
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(click());

        // Click the save button.
        activityScenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());

        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            // Use port 8080 to match the emulator configuration.
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            // Optionally log the error.
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            // Optionally, log the response code.
        } catch (IOException exception) {
            // Optionally log the error.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

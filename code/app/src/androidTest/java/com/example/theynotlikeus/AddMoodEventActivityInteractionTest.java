package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.Objects;

/**
 * UI tests for general interactions in AddMoodEventActivity.
 * These tests check inputs like a trigger that's too long, an invalid mood selection,
 * adding a photo, setting geolocation, and marking the event as public.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityInteractionTest extends com.example.theynotlikeus.FirestoreEmulatorTestBase {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testTriggerTooLongShows() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        activityScenario.onActivity(activity -> {
            View triggerView = activity.findViewById(R.id.editText_ActivityAddMoodEvent_triggerInput);
            if (triggerView instanceof android.widget.EditText) {
                ((android.widget.EditText) triggerView).setText("The Los Angeles Lakers are winning the NBA Championship in 2025.");
            }
        });

        // Click the save button.
        activityScenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    @Test
    public void testNoMoodSelectedShowsInvalidMood() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        // Replace the mood spinner adapter with an invalid selection.
        activityScenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner);
            String[] invalidMoodArray = { "" };
            ArrayAdapter<String> invalidAdapter = new ArrayAdapter<>(activity, R.layout.add_mood_event_spinner, invalidMoodArray);
            invalidAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
            moodSpinner.setAdapter(invalidAdapter);
        });

        // Click the save button.
        activityScenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    @Test
    public void testAddPhoto() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        onView(withId(R.id.button_ActivityAddMoodEvent_selectPhoto))
                .perform(click());

        Thread.sleep(2000);
    }

    @Test
    public void testSetGeolocation() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(click());

        Thread.sleep(2000);
    }

    @Test
    public void testMarkMoodAsPublic() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> activityScenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(click());

        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            // Log or handle the error if needed.
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            // Optionally, log the response code.
        } catch (IOException exception) {
            // Log or handle the error if needed.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

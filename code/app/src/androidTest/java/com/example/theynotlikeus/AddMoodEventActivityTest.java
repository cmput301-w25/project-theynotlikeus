package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

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
 * UI test for adding a mood event.
 * Launches the add mood event activity from the Home My Moods fragment
 * and verifies a valid mood event submission.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityTest extends com.example.theynotlikeus.FirestoreEmulatorTestBase  {

    /**
     * Scenario is in MainActivity.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    // The Firestore emulator configuration is now handled in FirestoreEmulatorTestBase.

    /**
     * Test: Trigger too long
     * @throws InterruptedException
     * @throws ArithmeticException
     */
    @Test
    public void testTriggerTooLongShows() throws InterruptedException, ArithmeticException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            View triggerView = activity.findViewById(R.id.editText_ActivityAddMoodEvent_triggerInput);
            if (triggerView instanceof android.widget.EditText) {
                ((android.widget.EditText) triggerView).setText("The Los Angeles Lakers are winning the NBA Championship in 2025.");
            }
        });

        // Click the save button.
        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    /**
     * Test: No Mood Selected Shows Invalid Mood
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    @Test
    public void testNoMoodSelectedShowsInvalidMood() throws InterruptedException, IllegalArgumentException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Replace the mood spinner adapter with an invalid selection.
        scenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner);
            // Simulate an invalid mood selection.
            String[] invalidMoodArray = { "" };
            ArrayAdapter<String> invalidAdapter = new ArrayAdapter<>(activity, R.layout.add_mood_event_spinner, invalidMoodArray);
            invalidAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
            moodSpinner.setAdapter(invalidAdapter);
        });

        // Click the save button.
        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    /**
     * Test: Add a photo.
     */
    @Test
    public void testAddPhoto() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.button_ActivityAddMoodEvent_selectPhoto))
                .perform(click());

        Thread.sleep(2000);
    }

    /**
     * Test: Set geolocation.
     */
    @Test
    public void testSetGeolocation() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(click());

        Thread.sleep(2000);
    }

    /**
     * Test: Mark mood event as public.
     */
    @Test
    public void testMarkMoodAsPublic() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(click());

        Thread.sleep(2000);
    }

    /**
     * Test: Valid Submission Finishes Activity.
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException, IllegalArgumentException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Adds a valid mood event.
        onView(withId(R.id.editText_ActivityAddMoodEvent_triggerInput))
                .perform(clearText(), typeText("Lakers"));

        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner))
                .perform(click());
        onData(is("Happiness")).perform(click());

        onView(withId(R.id.spinner_ActivityAddMoodEvent_socialsituation))
                .perform(click());
        onData(is("Alone")).perform(click());

        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(click());

        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(click());

        // Clicks the save button.
        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());

        Thread.sleep(2000);
    }

    /**
     * Tear down: Clear all documents from the emulator after each test.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

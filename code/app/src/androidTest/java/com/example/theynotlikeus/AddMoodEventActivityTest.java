package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static java.util.EnumSet.allOf;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * UI test for adding a mood event
 * Launches the add mood event activity from home my moods fragment
 * Then verifies a valid mood event submission
 *
*/

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        // Specific address for emulated device to access our localhost.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }


    // Test: Trigger Too Long
    @Test
    public void testTriggerTooLongShowsToast() throws ArithmeticException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        final View[] decorViewHolder = new View[1];
        scenario.onActivity(activity -> decorViewHolder[0] = activity.getWindow().getDecorView());

        // Enter an long trigger.
        onView(withId(R.id.edittext_ActivityAddMoodEvent_trigger))
                .perform(clearText(), typeText("The Los Angeles Lakers are winning the NBA Championship in 2025."));


        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());

        onView(withText("Trigger has too many characters!"))
                .inRoot(withDecorView(not(is(decorViewHolder[0]))))
                .check(matches(isDisplayed()));
    }


    // Test: No Mood Selected
    @Test
    public void testNoMoodSelectedShowsInvalidMoodToast() throws IllegalArgumentException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Replace the mood spinner adapter with an invalid selection.
        scenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);

            // Simulate an invalid mood selection.
            String[] invalidMoodArray = { "" };
            ArrayAdapter<String> invalidAdapter = new ArrayAdapter<>(activity, R.layout.add_mood_event_spinner, invalidMoodArray);
            invalidAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
            moodSpinner.setAdapter(invalidAdapter);
        });

        //
        final View[] decorViewHolder = new View[1];
        scenario.onActivity(activity -> decorViewHolder[0] = activity.getWindow().getDecorView());

        onView(withId(R.id.edittext_ActivityAddMoodEvent_trigger)).perform(clearText(), typeText("Lakers"));


        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());


        onView(withText("Invalid mood selection."))
                .inRoot(withDecorView(not(is(decorViewHolder[0]))))
                .check(matches(isDisplayed()));

    }


    // Test: Valid Submission
    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException, IllegalArgumentException{
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Adds a valid mood event
        onView(withId(R.id.edittext_ActivityAddMoodEvent_trigger))
                .perform(clearText(), typeText("Lakers"));

        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentmood))
                .perform(click());
        onData(is("Happiness")).perform(click());

        onView(withId(R.id.spinner_ActivityAddMoodEvent_socialsituation))
                .perform(click());
        onData(is("Alone")).perform(click());

        // clicks the save button
        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());

        Thread.sleep(2000);

        // Verifies that the activity finished.
        assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
    }


    // Tear down: Clear all documents from the emulator after each test
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

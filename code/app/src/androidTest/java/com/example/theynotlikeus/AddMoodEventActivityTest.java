package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.theynotlikeus.view.AddMoodEventActivity;
import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";  // Emulator's alias to host's localhost
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Test: Trigger text is too long.
     */
    @Test
    public void testTriggerTooLongShows() throws InterruptedException {
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

        SystemClock.sleep(2000);
    }

    /**
     * Test: No Mood Selected.
     */
    @Test
    public void testNoMoodSelectedShowsInvalidMood() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner);
            // Simulate an invalid mood selection by setting adapter with empty string.
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

        SystemClock.sleep(2000);
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

        SystemClock.sleep(2000);
    }

    /**
     * Test: Set geolocation toggle.
     */
    @Test
    public void testSetGeolocation() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(scrollTo(), click());

        SystemClock.sleep(2000);
    }

    /**
     * Test: Mark mood event as public by clicking the privacy switch.
     */
    @Test
    public void testMarkMoodAsPublic() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(scrollTo(), click());

        SystemClock.sleep(2000);
    }

    /**
     * Test: Valid Submission.
     */
    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Fill in the trigger input.
        onView(withId(R.id.editText_ActivityAddMoodEvent_triggerInput))
                .perform(clearText(), typeText("Lakers"));

        // For the mood spinner:
        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner))
                .perform(scrollTo(), click());
        onData(equalTo("Happiness"))
                .inRoot(isPlatformPopup())
                .perform(click());

        // For the social situation spinner:
        onView(withId(R.id.spinner_ActivityAddMoodEvent_socialsituation))
                .perform(scrollTo(), click());
        onData(equalTo("Alone"))
                .inRoot(isPlatformPopup())
                .perform(click());

        // Toggle geolocation and privacy.
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation))
                .perform(scrollTo(), click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy))
                .perform(scrollTo(), click());

        // Click the save button.
        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        SystemClock.sleep(2000);
    }

    /**
     * Tear down: Clear all documents from the Firestore emulator after each test.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        try {
            URL url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId +
                    "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.e("URL Error", Objects.requireNonNull(e.getMessage()));
        } catch (IOException e) {
            Log.e("IO Error", Objects.requireNonNull(e.getMessage()));
        }
    }
}

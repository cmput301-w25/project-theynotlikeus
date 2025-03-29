package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static com.bumptech.glide.Glide.init;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static java.util.regex.Pattern.matches;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.theynotlikeus.view.AddMoodEventActivity;
import com.example.theynotlikeus.view.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


/**
 * UI test for adding a mood event
 * Launches the add mood event activity from home my moods fragment
 * Then verifies a valid mood event submission
*/


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityTest {
    /**
     * Scenario is in MainActivity
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Set up Firestore locally
     */
    @BeforeClass
    public static void setup() {
        //Specific address for emulated device to access our localhost.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.useEmulator("10.0.2.2", 9299);
    }


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

        //click the save button.
        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);

    }

    /**
     * Test: No Mood Selected
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    @Test
    public void testNoMoodSelectedShowsInvalidMood() throws InterruptedException, IllegalArgumentException {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        //Replace the mood spinner adapter with an invalid selection.
        scenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner);

            //Simulate an invalid mood selection.
            String[] invalidMoodArray = { "" };
            ArrayAdapter<String> invalidAdapter = new ArrayAdapter<>(activity, R.layout.add_mood_event_spinner, invalidMoodArray);
            invalidAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
            moodSpinner.setAdapter(invalidAdapter);
        });

        //click the save button.
        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);

    }

    @Test
    public void testAddPhotoUploadsImage() throws Exception {
        //Initialize Espresso Intents if needed.
        init();

        //Launch the AddMoodEventActivity.
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        //Set the ImageView's drawable and update the activity's internal imageUri field.
        scenario.onActivity(activity -> {
            //Set the drawable resource manually.
            ImageView imageView = activity.findViewById(R.id.imageview_ActivityAddMoodEvent_photo);
            imageView.setImageResource(R.drawable.ic_add);

            //Create a dummy URI pointing to the drawable resource.
            Uri dummyUri = Uri.parse("android.resource://com.example.theynotlikeus/" + R.drawable.ic_add);

            //Use reflection to set the private imageUri field in the activity.
            try {
                Field imageUriField = AddMoodEventActivity.class.getDeclaredField("imageUri");
                imageUriField.setAccessible(true);
                imageUriField.set(activity, dummyUri);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        //Click the "Save" button to trigger the image upload.
        onView(withId(R.id.button_ActivityAddMoodEvent_save)).perform(click());

        //Wait for the upload process to complete (adjust timing as needed).
        Thread.sleep(5000);

        //Query Firebase Storage to verify the image was uploaded.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        //The app uploads the image to the "mood_images" folder.
        StorageReference moodImagesRef = storageRef.child("mood_images");

        //List all items in the "mood_images" folder.
        Task<ListResult> listTask = moodImagesRef.listAll();
        ListResult listResult = Tasks.await(listTask);

        //Assert that at least one item exists in the folder.
        assertTrue("No images were uploaded to Firebase Storage", listResult.getItems().size() > 0);

        //Release Espresso Intents.
        release();
    }






    /**
     * Test: Set geolocation
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
     * Test: Mark mood event as public
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
     * Test: Valid Submission
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException, IllegalArgumentException{
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        //Adds a valid mood event
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

        //clicks the save button
        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());

        Thread.sleep(2000);
    }


    /**
     *  Tear down: Clear all documents from the emulator after each test */
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

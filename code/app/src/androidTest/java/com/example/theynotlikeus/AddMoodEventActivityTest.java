package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.core.content.FileProvider;
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
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventActivityTest {

    // Launch MainActivity (from which you navigate to AddMoodEventActivity)
    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityScenario = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        // Configure Firebase emulators.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.useEmulator("10.0.2.2", 9299);
    }

    @Test
    public void testTriggerTooLongShows() throws InterruptedException {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            View triggerView = activity.findViewById(R.id.editText_ActivityAddMoodEvent_triggerInput);
            if (triggerView instanceof android.widget.EditText) {
                ((android.widget.EditText) triggerView).setText("The Los Angeles Lakers are winning the NBA Championship in 2025.");
            }
        });

        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    @Test
    public void testNoMoodSelectedShowsInvalidMood() throws InterruptedException {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            Spinner moodSpinner = activity.findViewById(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner);
            // Simulate an invalid mood selection.
            String[] invalidMoodArray = { "" };
            ArrayAdapter<String> invalidAdapter = new ArrayAdapter<>(activity,
                    R.layout.add_mood_event_spinner, invalidMoodArray);
            invalidAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
            moodSpinner.setAdapter(invalidAdapter);
        });

        scenario.onActivity(activity -> {
            View saveButton = activity.findViewById(R.id.button_ActivityAddMoodEvent_save);
            saveButton.performClick();
        });

        Thread.sleep(2000);
    }

    @Test
    public void testAddPhotoUploadsImage() throws Exception {
        // Initialize Espresso Intents.
        init();

        // Launch AddMoodEventActivity.
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // In the activity, set up the image from the drawable resource.
        scenario.onActivity(activity -> {
            // Set the ImageView's drawable to the desired image.
            ImageView imageView = activity.findViewById(R.id.imageview_ActivityAddMoodEvent_photo);
            imageView.setImageResource(R.drawable.ic_happy_emoticon);

            // Copy the drawable (ic_happy_emoticon.png) from res/drawable into a temporary file.
            File tempFile = new File(activity.getCacheDir(), "temp_image.png");
            try (InputStream is = activity.getResources().openRawResource(R.drawable.ic_happy_emoticon);
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp image file", e);
            }

            // Obtain a content URI for the temporary file using FileProvider.
            Uri contentUri = FileProvider.getUriForFile(
                    activity,
                    activity.getPackageName() + ".fileprovider",
                    tempFile);

            // Use reflection to set the private field "imageUri" in AddMoodEventActivity.
            try {
                Field imageUriField = AddMoodEventActivity.class.getDeclaredField("imageUri");
                imageUriField.setAccessible(true);
                imageUriField.set(activity, contentUri);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set imageUri field", e);
            }
        });

        // Click the "Save" button to trigger the upload.
        onView(withId(R.id.button_ActivityAddMoodEvent_save)).perform(click());

        // Allow some time for the upload process to start.
        Thread.sleep(5000);

        // Query Firebase Storage to verify the image was uploaded.
        // (This part assumes your upload logic uploads to the "mood_images" folder.)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference moodImagesRef = storageRef.child("mood_images");

        // Use a CountDownLatch to wait for the listAll() task to complete.
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final ListResult[] resultHolder = new ListResult[1];
        final Exception[] exceptionHolder = new Exception[1];

        moodImagesRef.listAll().addOnSuccessListener(result -> {
            resultHolder[0] = result;
            latch.countDown();
        }).addOnFailureListener(e -> {
            exceptionHolder[0] = e;
            latch.countDown();
        });

        // Wait up to 20 seconds for the query to complete.
        if (!latch.await(20, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timed out waiting for Firebase Storage query");
        }
        if (exceptionHolder[0] != null) {
            throw new RuntimeException("Failed to list Firebase Storage files", exceptionHolder[0]);
        }
        ListResult listResult = resultHolder[0];
        assertTrue("No images were uploaded to Firebase Storage", listResult.getItems().size() > 0);

        // Release Espresso Intents.
        release();
    }

    @Test
    public void testSetGeolocation() throws InterruptedException {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void testMarkMoodAsPublic() throws InterruptedException {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void testValidSubmissionFinishesActivity() throws InterruptedException {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AddMoodEventActivity.class);
        intent.putExtra("Lebron", "Luka");
        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.editText_ActivityAddMoodEvent_triggerInput))
                .perform(clearText(), typeText("Lakers"));

        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner))
                .perform(click());
        onData(is("Happiness")).perform(click());

        onView(withId(R.id.spinner_ActivityAddMoodEvent_socialsituation))
                .perform(click());
        onData(is("Alone")).perform(click());

        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_privacy)).perform(click());

        scenario.onActivity(activity -> activity.findViewById(R.id.button_ActivityAddMoodEvent_save).performClick());
        Thread.sleep(2000);
    }

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

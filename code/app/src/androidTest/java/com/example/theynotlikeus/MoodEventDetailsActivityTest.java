package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodEventDetailsActivityTest {

    private static String generatedMoodId; //MoodID to identify the mood
    private static String testDate;
    private static String testTime;
    private static String expectedDateString;  //Stores the full Date.toString() for the test's final check

    //Latitude and Longitude for the location attribute for when we implement geolocation, Part 4
    private static final double testLatitude = 37.7749;
    private static final double testLongitude = -122.4194;



    @BeforeClass
    public static void setup() {
        //Specific address for emulated device to access localhost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");


        Date now = new Date();
        expectedDateString = now.toString();

        //Create a Mood object
        Mood mood = new Mood();
        mood.setTrigger("Finished a great book");
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setMoodState(Mood.MoodState.HAPPINESS);
        mood.setDateTime(now);
        //mood.setLocation(testLatitude, testLongitude); When geolocation is implemented in part 4

        //Waiting for the emulator to populate
        CountDownLatch latch = new CountDownLatch(1);
        moodsRef.add(mood);
        latch.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        // If generatedMoodId wasn't set, stop
        if (generatedMoodId == null) {
            Log.e("MoodEvent", "Generated mood ID is null. Cannot launch activity.");
            return;
        }

        // Provide both context and the Activity class in the Intent constructor
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                MoodEventDetailsActivity.class
        );
        intent.putExtra("moodId", generatedMoodId);

        // Launch activity with the explicit Intent
        try (ActivityScenario<MoodEventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Now do your Espresso checks
            onView(withId(R.id.textview_ActivityMoodEventDetails_socialsituation))
                    .check(matches(withText("ALONE")));

            onView(withId(R.id.textview_ActivityMoodEventDetails_username))
                    .check(matches(withText("HAPPINESS")));

            onView(withId(R.id.textview_ActivityMoodEventDetails_triggervalue))
                    .check(matches(withText("Finished a great book")));

            // Now we check the EXACT .toString() output:
            onView(withId(R.id.textview_ActivityMoodEventDetails_dateandtime))
                    .check(matches(withText(expectedDateString)));

            /*
            String expectedCoordinates = testLatitude + ", " + testLongitude;
            onView(withId(R.id.textview_ActivityMoodEventDetails_location))
                    .check(matches(withText(expectedCoordinates)));
            */
            /*
            onView(withId(R.id.imageview_ActivityMoodEventDetails_moodimage))
                    .check(matches(withId(R.drawable.ic_happy_emoticon)));

             */
        }
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

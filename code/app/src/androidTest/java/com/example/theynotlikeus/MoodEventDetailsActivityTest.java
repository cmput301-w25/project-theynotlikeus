package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
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
    private static String generatedMoodId;//MoodID to identify the mood
    private static String testDate;
    private static String testTime;
    //Latitude and Longitude for the location attribute
    private static double testLatitude = 37.7749;
    private static double testLongitude = -122.4194;

    @BeforeClass
    public static void setup() {
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        //Creating a formatted date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        testDate = dateFormat.format(now);
        testTime = timeFormat.format(now);

        //Storing a mood object with its attributes in Firestore
        Mood mood = new Mood();
        mood.setTrigger("Finished a great book"); //trigger
        mood.setSocialSituation(Mood.SocialSituation.ALONE); //social situation
        mood.setMoodState(Mood.MoodState.HAPPINESS); //moodstate
        mood.setDateTime(now); //date and time
        mood.setLocation(testLatitude, testLongitude);//location

        moodsRef.add(mood);
    }

    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {

        if (generatedMoodId == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("MOOD_ID", generatedMoodId);
        ActivityScenario<MoodEventDetailsActivity> scenario = ActivityScenario.launch(intent);

        // Check if mood details are displayed correctly
        onView(withId(R.id.textview_ActivityMoodEventDetails_socialsituation)).check(matches(withText("ALONE"))); //social situation
        onView(withId(R.id.textview_ActivityMoodEventDetails_username)).check(matches(withText("HAPPINESS"))); //moodState
        onView(withId(R.id.textview_ActivityMoodEventDetails_triggervalue)).check(matches(withText("Finished a great book"))); //trigger
        onView(withId(R.id.textview_ActivityMoodEventDetails_dateandtime)).check(matches(withText(testDate + " " + testTime)));//date and time

        String expectedCoordinates = testLatitude + ", " + testLongitude;
        onView(withId(R.id.textview_ActivityMoodEventDetails_location)).check(matches(withText(expectedCoordinates))); //location

        onView(withId(R.id.imageview_ActivityMoodEventDetails_moodimage)).check(matches(withId(R.drawable.ic_happy_emoticon))); //icon displayed

    }



    @After
    public void tearDown() {

        String projectId = "theynotlikeus-6a9f1";
        URL url;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("FirestoreCleanup", "Response Code: " + response);
            urlConnection.disconnect();
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        }
    }
}

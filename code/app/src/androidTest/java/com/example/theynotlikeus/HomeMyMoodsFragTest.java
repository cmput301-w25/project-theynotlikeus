package com.example.theynotlikeus;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeMyMoodsFragTest {
    private List<Date> dateInstance;
    private static String testUsername = "Username";

    /* How to putExtra data using ActivityScenarioRule: https://stackoverflow.com/questions/54179560/how-to-putextra-data-using-newest-activityscenariorule-activityscenarioespress
     * Authored by: Jose Leles
     * Taken by: Ercel Angeles
     * Taken on: March 8, 2025
     */
    
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", testUsername);
        intent.putExtras(bundle);
    }

    @Rule  // Change depending on the activity
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(intent);

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    // Seed the database
    @Before
    public void seedDatabase() throws InterruptedException {
        // Initialize the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        // Initialize the mood events
        int numOfMoodEvents = 2;
        Mood.MoodState currentMoodState;
        dateInstance = new ArrayList<>();   // Date and Time examples.   Ex: Date date1 = dateInstance.get(1);
        String[] moodStateList = {"HAPPINESS", "ANGER"};    // Mood state examples
        // Trigger examples
        String[] moodTriggerTests = {
                "This was a triumph",
                "I'm not even angry"
        };
        // Social situation examples
        String[] moodSocialSituationTests = {
                "TO_CROWD",
                "ONE_TO_OTHER"
        };
        // Initialize all moods and put into database
        for (int i = 0; i < numOfMoodEvents; i++) {
            Date currentDate = new Date();  // Create new date
            dateInstance.add(currentDate);  // Add current date to date instance
            currentMoodState = Mood.MoodState.valueOf(moodStateList[i]); // Get mood state
            Mood currentMoodEvent = new Mood(currentDate, currentMoodState);    // Create a mood event using date and mood state

            // Add other details
            currentMoodEvent.setTrigger(moodTriggerTests[i]);
            currentMoodEvent.setSocialSituation(Mood.SocialSituation.valueOf(moodSocialSituationTests[i]));
            currentMoodEvent.setUsername(testUsername); // Keep this commented
            DocumentReference docRef = moodsRef.document();
            currentMoodEvent.setDocId(docRef.getId());
            docRef.set(currentMoodEvent);
        }

        Thread.sleep(2000);     // Very important or test will fail.
    }

    @Test
    public void appShouldDisplayExistingMoodEventsOnLaunch() {
        // Check if mood title shows
        onView(withText("HAPPINESS")).check(matches(isDisplayed()));
        onView(withText("ANGER")).check(matches(isDisplayed()));

        // Check if triggers show
        onView(withText("This was a triumph")).check(matches(isDisplayed()));
        onView(withText("I'm not even angry")).check(matches(isDisplayed()));

        // Check if social situations show
        onView(withText("TO_CROWD")).check(matches(isDisplayed()));
        onView(withText("ONE_TO_OTHER")).check(matches(isDisplayed()));
        onView(withText("Welcome, " + testUsername + "!")).check(matches(isDisplayed()));

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

package com.example.theynotlikeus;


import static android.app.PendingIntent.getActivity;
import static android.content.Intent.getIntent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.RootMatchers;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeMyMoodsFragTest {

    private String testUsername;
    private Date date;

    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setUpBase() {

        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }
    // Seed the database
    @Before
    public void seedDatabase() throws InterruptedException {
        Log.i("seedingBegin", "Started seeding data...");
        // Initialize the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        // Initialize the mood events
        int numOfAutoMoodEvents = 2;
        Mood.MoodState currentMoodState;
        String[] moodStateList = {"HAPPINESS", "ANGER", "SADNESS"};    // Mood state examples
        // Trigger examples
        String[] moodTriggerTests = {
                "This was a triumph",
                "I'm not even angry",
                "Go ahead and leave me"
        };
        // Social situation examples
        String[] moodSocialSituationTests = {
                "TO_CROWD",
                "ONE_TO_OTHER",
                "ALONE"
        };

        // Initialize all moods and put into database
        for (int i = 0; i < numOfAutoMoodEvents; i++) {
            currentMoodState = Mood.MoodState.valueOf(moodStateList[i]); // Get mood state
            Mood currentMoodEvent = new Mood(currentMoodState);    // Create a mood event using date and mood state
            currentMoodEvent.setTrigger(moodTriggerTests[i]);      // Add other details
            currentMoodEvent.setSocialSituation(Mood.SocialSituation.valueOf(moodSocialSituationTests[i]));
            //currentMoodEvent.setUsername(testUsername);
            DocumentReference docRef = moodsRef.document();
            currentMoodEvent.setDocId(docRef.getId());
            docRef.set(currentMoodEvent);
        }

        /* Convert String to Date format: https://docs.vultr.com/java/examples/convert-string-to-date
         * Authored by: Vultr
         * Taken by: Ercel Angeles
         * Taken on: March 9, 2025
         */
        String dateString = "Jan 09, 1999";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentMoodState = Mood.MoodState.valueOf(moodStateList[2]);
        Mood currentMoodEvent = new Mood(date, currentMoodState);
        currentMoodEvent.setTrigger(moodTriggerTests[2]);      // Add other details
        currentMoodEvent.setSocialSituation(Mood.SocialSituation.valueOf(moodSocialSituationTests[2]));
        DocumentReference docRef = moodsRef.document();
        currentMoodEvent.setDocId(docRef.getId());
        docRef.set(currentMoodEvent);

        Log.i("seedingEnd", "seedDatabase() has finished.");
        Thread.sleep(2000);     // Very important or test will fail.
    }

    @Test
    public void testUI() {

        // Check if mood title shows
        onView(withText("HAPPINESS")).check(matches(isDisplayed()));
        onView(withText("ANGER")).check(matches(isDisplayed()));

        // Check if triggers show
        onView(withText("This was a triumph")).check(matches(isDisplayed()));
        onView(withText("I'm not even angry")).check(matches(isDisplayed()));
        onView(withText("Go ahead and leave me")).check(matches(isDisplayed()));

        // Check if social situations show
        onView(withText("TO_CROWD")).check(matches(isDisplayed()));
        onView(withText("ONE_TO_OTHER")).check(matches(isDisplayed()));

        // Check if the fragment welcomes the user
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).check(matches(isNotChecked())).perform(click());
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).check(matches(isChecked()));   // Check if the checkbox is checked

        onView(withText("Go ahead and leave me")).check(doesNotExist());

        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).check(matches(isChecked())).perform(click());
        /* How to click on an item in autoCompleteTextView: https://stackoverflow.com/questions/38562341/espresso-autocompletetextview-click
         * Authored by: Akbolat SSS
         * Taken by: Ercel Angeles
         * Taken on: March 9, 2025
         */
        onView(withId(R.id.autoCompleteTextView)).perform(click());
        onView(withText("Sadness"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(R.id.autoCompleteTextView)).perform(click()).perform(replaceText("All Moods"));
        onView(withText("All Moods"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        Espresso.pressBack();



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

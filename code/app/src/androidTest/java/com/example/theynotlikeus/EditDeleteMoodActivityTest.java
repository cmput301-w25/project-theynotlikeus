package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;

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

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditDeleteMoodActivityTest {
    private static String testMoodId;
    private static FirebaseFirestore db;
    private static String testDate;
    private static String testTime;
    //Latitude and Longitude for the location attribute
    private static double testLatitude =1.02;
    private static double testLongitude =8.18;
    @BeforeClass
    public static void setup() {
        db = FirebaseFirestore.getInstance();
        db.useEmulator("127.0.0.1", 8089); // Updated to match new Firestore emulator port
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        CollectionReference moodsRef = db.collection("moods");
        CountDownLatch latch = new CountDownLatch(1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        testDate = dateFormat.format(now);
        testTime = timeFormat.format(now);

        //Storing a mood object with its attributes in Firestore
        Mood mood = new Mood();
        mood.setTrigger("test trigger");
        mood.setSocialSituation(Mood.SocialSituation.ALONE); //social situation
        mood.setMoodState(Mood.MoodState.HAPPINESS); //moodstate
        mood.setDateTime(now); //date and time
        mood.setLocation(testLatitude, testLongitude);//location

        moodsRef.add(mood);

        latch.await(3, TimeUnit.SECONDS);
    }

    @Test
    public void testEditMood() {
        Intent intent = new Intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), EditDeleteMoodActivity.class);
        intent.putExtra("moodId", testMoodId);
        ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent);


        onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput)).perform(replaceText("test trigger"));
        onView(withId(R.id.button_DeleteEditMoodActivity_save)).perform(click());

        db.collection("moods").document(testMoodId).get().addOnSuccessListener(documentSnapshot -> {
            matches(withText("test trigger"));
        });
    }

    @Test
    public void testDeleteMood() {
        Intent intent = new Intent();
        intent.putExtra("moodId", testMoodId);
        ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.imageButton_DeleteEditMoodActivity_delete)).perform(click());

        db.collection("moods").document(testMoodId).get().addOnSuccessListener(documentSnapshot -> {
            matches(withText(""));
        });
    }

    @After
    public void tearDown() {
        try {
            String projectId = "theynotlikeus-6a9f1";
            URL url = new URL("http://127.0.0.1:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.getResponseCode();
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("Cleanup Error", e.getMessage());
        }
    }
}

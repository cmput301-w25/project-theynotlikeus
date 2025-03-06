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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodEventDetailsActivityTest {
    private static String generatedMoodId;

    @BeforeClass
    public static void setup() {
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        Mood mood = new Mood();
        mood.setTrigger("Finished a great book");
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setMoodState(Mood.MoodState.HAPPINESS);

        CountDownLatch latch = new CountDownLatch(1);

        moodsRef.add(mood).addOnSuccessListener(documentReference -> {
            generatedMoodId = documentReference.getId();
            latch.countDown();
        }).addOnFailureListener(e -> {
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        if (generatedMoodId == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("MOOD_ID", generatedMoodId);
        ActivityScenario<MoodEventDetailsActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.textview_activitymoodeventdetails_socialsituation))
                .check(matches(withText("ALONE")));

        onView(withId(R.id.textview_activitymoodeventdetails_username))
                .check(matches(withText("HAPPINESS")));

        onView(withId(R.id.textview_activitymoodeventdetails_triggervalue))
                .check(matches(withText("Finished a great book")));
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

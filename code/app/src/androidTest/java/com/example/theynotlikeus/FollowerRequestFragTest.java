package com.example.theynotlikeus;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.theynotlikeus.model.Request;
import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class FollowerRequestFragTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        // Remove the emulator configuration call here because it's already set in CustomTestRunner.
        // FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8089);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a follow request using your Request model.
        Request followRequest = new Request();
        followRequest.setFollowee("wasp");
        followRequest.setFollower("nymur");

        CountDownLatch latch = new CountDownLatch(1);
        db.collection("request")
                .add(followRequest)
                .addOnSuccessListener(documentReference -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void appShouldDisplayExistingFollowRequestOnLaunch() {
        // Create an Intent with the required extra.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "wasp");

        // Launch MainActivity with the custom intent.
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {

            // Navigate to the FollowerRequestFrag by clicking on the bottom navigation item "Requests".
            onView(withId(R.id.nav_profile)).perform(click());

            // Add a short delay so Firestore can load and populate the RecyclerView.
            SystemClock.sleep(2000);

            // Verify that the title TextView is displayed and not empty.
            onView(withId(R.id.textview_FollowerRequestFrag_title))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.textview_FollowerRequestFrag_title))
                    .check(matches(not(withText(""))));

            // Verify that the RecyclerView displays an item with the text "nymur".
            onView(withText("nymur")).check(matches(isDisplayed()));

            // Verify that the FloatingActionButton is visible.
            onView(withId(R.id.floatingActionButton_FollowerRequestFrag_addfollow))
                    .check(matches(isDisplayed()));
        }
    }

    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1"; // Your Firebase project ID.
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/"
                    + projectId
                    + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) Objects.requireNonNull(url).openConnection();
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

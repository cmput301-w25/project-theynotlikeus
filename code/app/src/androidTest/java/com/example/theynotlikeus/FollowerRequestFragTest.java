package com.example.theynotlikeus;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.theynotlikeus.model.Request;
import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.After;
import org.junit.Before;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class FollowerRequestFragTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        //Point Firebase to the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Seed the Firestore database before each test.
     * Here we add a follow request using your Request model with followee "wasp" and follower "nymur".
     */
    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Create a follow request using your Request model.
        Request followRequest = new Request();
        followRequest.setFollowee("wasp");
        followRequest.setFollower("nymur");

        CountDownLatch latch = new CountDownLatch(1);
        db.collection("request")
                .add(followRequest)
                .addOnSuccessListener(documentReference -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());
        latch.await(30, TimeUnit.SECONDS);
    }

    /**
     * Test: Display existing follow requests on launch.
     *
     * This test launches MainActivity with an intent extra "username" set to "wasp".
     * It then verifies that the fragment displays:
     * - A non-empty title,
     * - An item with the follower "nymur" in the RecyclerView,
     * - The FloatingActionButton is visible.
     */
    @Test
    public void appShouldDisplayExistingFollowRequestOnLaunch() {
        //Create an Intent with the required extra.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "wasp");

        //Launch MainActivity with the custom intent.
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            //Espresso assertions to verify UI components.

            //Verify that the title TextView is displayed and not empty.
            onView(withId(R.id.textview_FollowerRequestFrag_title))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.textview_FollowerRequestFrag_title))
                    .check(matches(not(withText(""))));

            //Verify that the RecyclerView displays an item with the text "nymur".
            onView(withText("nymur")).check(matches(isDisplayed()));

            //Verify that the FloatingActionButton is visible.
            onView(withId(R.id.floatingActionButton_FollowerRequestFrag_addfollow))
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Clean up the Firestore emulator after each test.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1"; //Your Firebase project ID.
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

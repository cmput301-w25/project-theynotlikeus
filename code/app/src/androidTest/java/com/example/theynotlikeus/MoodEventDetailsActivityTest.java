package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.theynotlikeus.MoodEventDetailsActivityTest.DrawableMatchers.withDrawable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.view.MoodEventDetailsActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI Test: MoodEventDetailsActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodEventDetailsActivityTest {

    // Static variable to hold the generated Mood document ID.
    private static String generatedMoodId;
    private static String expectedDateString;

    // Latitude and Longitude for location attribute (for part 4)
    private static final double testLatitude = 37.7749;
    private static final double testLongitude = -122.4194;

    /**
     * Set up Firestore.
     *
     * Note: We remove the call to useEmulator() here because CustomTestRunner already configures Firestore.
     */
    @BeforeClass
    public static void setup() {
        // No need to call useEmulator() here.
    }

    /**
     * Seed the database.
     */
    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        Date now = new Date();
        expectedDateString = now.toString();

        // Create a Mood object.
        Mood mood = new Mood();
        mood.setTrigger("Finished a great book");
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setMoodState(Mood.MoodState.HAPPINESS);
        mood.setDateTime(now);
        // mood.setLocation(testLatitude, testLongitude); // Uncomment when geolocation is implemented

        // Use a CountDownLatch to wait for Firestore to add the document and assign the generated ID.
        CountDownLatch latch = new CountDownLatch(1);
        moodsRef.add(mood)
                .addOnSuccessListener(documentReference -> {
                    generatedMoodId = documentReference.getId();
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("SeedDatabase", "Failed to add mood", e);
                    latch.countDown();
                });
        latch.await(30, TimeUnit.SECONDS);
    }

    /**
     * Test: Display existing mood on launch.
     */
    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        // Check if generatedMoodId was successfully set.
        if (generatedMoodId == null) {
            Log.e("MoodEvent", "Generated mood ID is null. Cannot launch activity.");
            return;
        }

        // Launch MoodEventDetailsActivity with the generated mood ID.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MoodEventDetailsActivity.class);
        intent.putExtra("moodId", generatedMoodId);
        try (ActivityScenario<MoodEventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify UI elements display the correct mood details.
            onView(withId(R.id.textview_ActivityMoodEventDetails_socialsituation))
                    .check(matches(withText("ALONE")));
            onView(withId(R.id.textview_ActivityMoodEventDetails_username))
                    .check(matches(withText("HAPPINESS")));
            onView(withId(R.id.textview_ActivityMoodEventDetails_triggervalue))
                    .check(matches(withText("Finished a great book")));
            onView(withId(R.id.textview_ActivityMoodEventDetails_dateandtime))
                    .check(matches(withText(expectedDateString)));

            /* Geolocation for part 4 (uncomment when implemented)
            String expectedCoordinates = testLatitude + ", " + testLongitude;
            onView(withId(R.id.textview_ActivityMoodEventDetails_location))
                    .check(matches(withText(expectedCoordinates)));
            */

            // Verify the correct mood icon is displayed.
            onView(withId(R.id.imageview_ActivityMoodEventDetails_moodimage))
                    .check(matches(withDrawable(R.drawable.ic_happy_emoticon)));
        }
    }

    /**
     * Tear down database.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1"; // Your Firebase project ID.
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
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

    /**
     * Helper function for matching drawable.
     */
    public static class DrawableMatchers {
        /**
         * Returns a matcher that checks if an ImageView is displaying a given drawable resource.
         * This class converts the drawables to bitmaps and compares them.
         * See: https://stackoverflow.com/questions/9125229/comparing-two-drawables-in-android
         */
        public static Matcher<View> withDrawable(final int resourceId) {
            return new TypeSafeMatcher<View>() {
                @Override
                protected boolean matchesSafely(View view) {
                    if (!(view instanceof ImageView)) {
                        return false;
                    }
                    ImageView imageView = (ImageView) view;
                    Drawable expectedDrawable = view.getContext().getResources().getDrawable(resourceId);
                    Drawable actualDrawable = imageView.getDrawable();

                    if (expectedDrawable == null || actualDrawable == null) {
                        return false;
                    }

                    Bitmap actualBitmap = getBitmapFromDrawable(actualDrawable);
                    Bitmap expectedBitmap = getBitmapFromDrawable(expectedDrawable);

                    return actualBitmap.sameAs(expectedBitmap);
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("with drawable resource id: " + resourceId);
                }

                private Bitmap getBitmapFromDrawable(Drawable drawable) {
                    Bitmap bitmap = Bitmap.createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            Bitmap.Config.ARGB_8888
                    );
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                    return bitmap;
                }
            };
        }
    }
}

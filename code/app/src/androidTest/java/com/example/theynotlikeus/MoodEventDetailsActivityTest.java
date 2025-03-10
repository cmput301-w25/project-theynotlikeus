package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI Test: MoodEventDetailsActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodEventDetailsActivityTest {

    private static String generatedMoodId; //MoodID to identify the mood
    private static String expectedDateString;

    //Latitude and Longitude for the location attribute for when we implement geolocation, Part 4
    private static final double testLatitude = 37.7749;
    private static final double testLongitude = -122.4194;

    /**
     * Set up Firestore
     */
    @BeforeClass
    public static void setup() {
        //Specific address for emulated device to access localhost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }
    /**
     * Seed the database
     */
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

    /**
     * Test: display existing moods on launch
     */
    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        //Error handling: when the moodId wasnt generated properly
        if (generatedMoodId == null) {
            Log.e("MoodEvent", "Generated mood ID is null. Cannot launch activity.");
            return;
        }

        //Launching the activity manually to prevent a null pointer exception
        //due to the activity starting before a valid moodId was generated
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                MoodEventDetailsActivity.class
        );
        intent.putExtra("moodId", generatedMoodId);
        try (ActivityScenario<MoodEventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            //Testing whether all the UI elements display mood details
            onView(withId(R.id.textview_ActivityMoodEventDetails_socialsituation)).check(matches(withText("ALONE"))); //social situation
            onView(withId(R.id.textview_ActivityMoodEventDetails_username)).check(matches(withText("HAPPINESS"))); //moodstate
            onView(withId(R.id.textview_ActivityMoodEventDetails_triggervalue)).check(matches(withText("Finished a great book"))); //trigger value
            onView(withId(R.id.textview_ActivityMoodEventDetails_dateandtime)).check(matches(withText(expectedDateString))); //date and time

            /* Geolocation for part 4
            String expectedCoordinates = testLatitude + ", " + testLongitude;
            onView(withId(R.id.textview_ActivityMoodEventDetails_location))
                    .check(matches(withText(expectedCoordinates)));
            */
            onView(withId(R.id.imageview_ActivityMoodEventDetails_moodimage)).check(matches(withDrawable(R.drawable.ic_happy_emoticon))); //mood icon
        }
    }

    /**
     * Tear down database
     */
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
     * Helper function for matching drawable
     */
    public static class DrawableMatchers {
        /**
         * Returns a matcher that checks if an ImageView is displaying a given drawable resource.
         * This class is used to check whether the right mood icon is displayed by converting the icons into
         * bitmaps
         * https://stackoverflow.com/questions/9125229/comparing-two-drawables-in-android for helper functions and approach
         */
        public static Matcher<View> withDrawable(final int resourceId) {
            return new TypeSafeMatcher<View>() {
                @Override
                protected boolean matchesSafely(View view) {
                    if (!(view instanceof ImageView)) {
                        return false;
                    }
                    ImageView imageView = (ImageView) view;
                    Drawable expectedDrawable = view.getContext().getResources().getDrawable(resourceId);//expected icon
                    Drawable actualDrawable = imageView.getDrawable();//actual icon

                    if (expectedDrawable == null || actualDrawable == null) {
                        return false;
                    }

                    //Comparing the icons as bitmaps
                    Bitmap actualBitmap = getBitmapFromDrawable(actualDrawable);
                    Bitmap expectedBitmap = getBitmapFromDrawable(expectedDrawable);

                    return actualBitmap.sameAs(expectedBitmap);
                }


                @Override
                public void describeTo(Description description) {
                    //This is used to handle errors, when an error occurs we can identify the resourceId associated with the error
                    description.appendText("with drawable resource id: " + resourceId);
                }

                private Bitmap getBitmapFromDrawable(Drawable drawable) {
                    //converts the icon into a bitmap
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

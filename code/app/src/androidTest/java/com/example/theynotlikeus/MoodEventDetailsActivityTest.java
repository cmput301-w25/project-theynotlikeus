package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;

import android.content.Intent;
import android.graphics.Movie;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.theynotlikeus.MainActivity;
import com.example.theynotlikeus.Mood;
import com.example.theynotlikeus.MoodEventDetailsActivity;
import com.example.theynotlikeus.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodEventDetailsActivityTest {
    @Rule
    public ActivityScenarioRule<MoodEventDetailsActivity> scenario = new
            ActivityScenarioRule<MoodEventDetailsActivity>(MoodEventDetailsActivity.class);

    @BeforeClass
    public static void setup(){
        //Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");

        //Create Mood objects with test data
        Mood moods = new Mood();




        moods.setTrigger("Finished a great book");
        moods.setSocialSituation(Mood.SocialSituation.ALONE);
        moods.setMoodState(Mood.MoodState.HAPPINESS);
        //mood.setReason("Feeling inspired");
        /*
        moods.setUsername("User1");
        Date newDate=new Date();
        moods.setDateTime(newDate);
                 */
        //mood.setDateTime(formattedDate); Figure out a way to insert a date


        moodsRef.add(moods);

    }


    @Test
    public void appShouldDisplayExistingMoodOnLaunch() {
        onView(withId(R.id.textview_activitymoodeventdetails_socialsituation)).check((ViewAssertion)withText("ALONE"));
        //onView(withId(R.id.textview_activitymoodeventdetails_triggervalue)).check(matches(withText("Finished a great book")));
        onView(withId(R.id.textview_activitymoodeventdetails_username)).check((ViewAssertion) withText("HAPPINESS"));
        //onView(withId(R.id.textview_activitymoodeventdetails_dateandtime)).check((newDate));
        //onView(withId(R.id.edit_title)).check(matches(withText("Feeling inspired"))); No reasons
        //onView(withId(R.id.edit_title)).check(matches(isDisplayed())); Figure out a way to check the date
    }



    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
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








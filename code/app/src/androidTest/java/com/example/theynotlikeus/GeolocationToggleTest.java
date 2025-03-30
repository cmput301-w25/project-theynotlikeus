package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.AddMoodEventActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
* Test the geolocation button in AddMoodEventActivity
*Check for on and off
*
* */


@RunWith(AndroidJUnit4.class)
public class GeolocationToggleTest {



    @Test
    public void testGeolocationToggle() throws InterruptedException {
        // Launch AddMoodEventActivity manually
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddMoodEventActivity.class);
        intent.putExtra("username", "testuser");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);


        Thread.sleep(1000); // Wait for UI to render


        // Toggle ON
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isChecked()));


        // Toggle OFF
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isNotChecked()));
    }


}
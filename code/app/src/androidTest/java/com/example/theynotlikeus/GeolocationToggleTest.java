package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.theynotlikeus.view.AddMoodEventActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
* Test the geolocation button in AddMoodEventActivity
*Check for on and off
*
* */


@RunWith(AndroidJUnit4.class)
public class GeolocationToggleTest {


    @Rule
    public ActivityTestRule<AddMoodEventActivity> activityRule =
            new ActivityTestRule<>(AddMoodEventActivity.class, true, false);



    @Test
    public void testGeolocationSwitchToggle() throws InterruptedException {
        // Add dummy username
        Intent intent = new Intent();
        intent.putExtra("username", "testuser");
        activityRule.launchActivity(intent);

        // Toggle the geolocation switch on
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());

        // Check that if it's checked
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isChecked()));

        // Toggle off again and check
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isNotChecked()));
    }


}

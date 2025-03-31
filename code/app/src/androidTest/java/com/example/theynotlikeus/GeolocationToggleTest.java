package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.AddMoodEventActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the geolocation toggle in AddMoodEventActivity.
 * Verifies that toggling the geolocation switch works correctly.
 */
@RunWith(AndroidJUnit4.class)
public class GeolocationToggleTest {

    @Test
    public void testGeolocationToggle() {
        // Launch AddMoodEventActivity.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddMoodEventActivity.class);
        intent.putExtra("username", "testuser");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        // Wait until the geolocation switch is displayed.
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isDisplayed()));

        // Toggle ON: click the switch and verify it becomes checked.
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isChecked()));

        // Toggle OFF: click the switch again and verify it becomes unchecked.
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).perform(click());
        onView(withId(R.id.switch_ActivityAddMoodEvent_geolocation)).check(matches(isNotChecked()));
    }
}

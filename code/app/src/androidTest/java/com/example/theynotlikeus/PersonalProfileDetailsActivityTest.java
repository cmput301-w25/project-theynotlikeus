package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PersonalProfileDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<PersonalProfileDetailsActivity> scenario =
            new ActivityScenarioRule<>(PersonalProfileDetailsActivity.class);

    /*
    @Test
    public void testBackButtonClosesActivity() {
        // Click on the back button
        onView(withId(R.id.button_PersonalProfileDetailsActivity_back)).perform(click());

        // Verify that the activity is closed
        onView(withId(R.id.main)).check(doesNotExist());
    }
*/
    @Test
    public void testActivityLayoutLoadsCorrectly() {
        // Verify that the main container view is displayed
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}

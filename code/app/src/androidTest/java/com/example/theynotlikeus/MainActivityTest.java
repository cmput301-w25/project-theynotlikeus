package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testBottomNavigationSwitchesFragments() {
        // Click on Home navigation item
        onView(withId(R.id.nav_home)).perform(click());
        onView(withId(R.id.recyclerview_fragmenthomemymoods_userrecyclerview)).check(matches(isDisplayed()));

        // Click on Profile navigation item
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.textView_FollowerRequestFrag)).check(matches(isDisplayed()));

        // Click on Map navigation item
        onView(withId(R.id.nav_map)).perform(click());
        onView(withId(R.id.textView_HomeMapFragment)).check(matches(isDisplayed()));

        // Click on Community navigation item
        onView(withId(R.id.nav_community)).perform(click());
        onView(withId(R.id.textView_CommunityFrag)).check(matches(isDisplayed()));
    }

    @Test
    public void testActivityLaunchesSuccessfully() {
        // Verify that the main container view is displayed when activity starts
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToAddMoodEvent() {
        // Click on Home navigation item to ensure we are in the right place
        onView(withId(R.id.nav_home)).perform(click());

        // Navigate to Add Mood Event
        onView(withId(R.id.nav_mood_event)).perform(click());
        onView(withId(R.id.textview_ActivityAddMoodEvent_title)).check(matches(isDisplayed()));
    }
}

package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI Test for MainActivity
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    /**
     * Set scenario to be MainActivity
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Test: bottom navigation switches to fragment
     */
    @Test
    public void testBottomNavigationSwitchesFragments() {
        //Checking whether the home button works by checking if the main activity loads properly
        onView(withId(R.id.nav_home)).perform(click());
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)).check(matches(isDisplayed()));

        //Verifying whether the PersonalProfileDetailsActivity launches when the profile icon is clicked
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.textView_FollowerRequestFrag)).check(matches(isDisplayed()));

        //Verifying whether the the HomeMapFragment launches when the map icon is clicked
        onView(withId(R.id.nav_map)).perform(click());
        onView(withId(R.id.textView_HomeMapFragment)).check(matches(isDisplayed()));

        //Verifying whether the the CommunityFragment launches when the community icon is clicked
        onView(withId(R.id.nav_community)).perform(click());
        onView(withId(R.id.textView_CommunityFrag)).check(matches(isDisplayed()));
    }

    /**
     * Test: Activity launches successfully
     */
    @Test
    public void testActivityLaunchesSuccessfully() {
        //Verifying that the main container view is displayed when activity starts
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    /**
     * Test: Click home navigation to add mood event
     */
    @Test
    public void testNavigationToAddMoodEvent() {
        //Click on Home navigation item to ensure we are in the right place
        onView(withId(R.id.nav_home)).perform(click());

        //Navigate to Add Mood Event
        onView(withId(R.id.floatingActionButton_HomeMyMoodsFragment_addmood)).perform(click());
        onView(withId(R.id.imagebutton_ActivityAddMoodEvent_selectimage)).check(matches(isDisplayed()));
    }
}

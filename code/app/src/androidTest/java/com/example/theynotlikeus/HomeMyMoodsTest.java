package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeMyMoodsTest {

    /**
     * Creates an intent for MainActivity with the required username extra.
     */
    private static Intent getMainActivityIntent() {
        Context targetContext = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(targetContext, MainActivity.class);
        intent.putExtra("username", "testuser");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(getMainActivityIntent());

    /**
     * Simple test that launches the MainActivity and verifies the welcome text is displayed.
     */
    @Test
    public void testPageOpens() {
        onView(withId(R.id.textView_HomeMyMoodsFragment_welcomeUser))
                .check(matches(isDisplayed()))
                .check(matches(withText("Welcome, testuser!")));
    }
}

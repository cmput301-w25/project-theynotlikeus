package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI test for the login flow.
 * This test launches LoginActivity, navigates from the LoginUserSelectionFrag to the UserLoginFrag by clicking the "User" button,
 * then verifies both successful and failed login attempts.
 */
@RunWith(AndroidJUnit4.class)
public class UserLoginFragTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginSuccess() {
        // Navigate from LoginUserSelectionFrag to UserLoginFrag.
        onView(withId(R.id.button_user)).perform(click());

        // Input correct credentials in UserLoginFrag.
        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("nour"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.button_UserLogIn_SignIn)).perform(click());

        // Verify that HomeMyMoodsFrag is displayed by checking for a view that's part of that fragment.
        // For example, verify the welcome text is visible.
        onView(withId(R.id.textView_homeMyMoodFrag_welcomeUser))
                .check(matches(withText("Welcome, nour!")));
    }

    @Test
    public void testLoginFailure() {
        // From the login selection screen, click on the "User" button to open the login fragment.
        onView(withId(R.id.button_user)).perform(click());

        // In the UserLoginFrag, enter incorrect credentials.
        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("wrongUsername"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("wrongPassword"));

        // Click on the sign-in button.
        onView(withId(R.id.button_UserLogIn_SignIn)).perform(click());

        // Verify that the login fragment is still displayed by checking for a view with id "fragment_user_login_layout".
        onView(withId(R.id.fragment_user_login_layout)).check(matches(isDisplayed()));
    }
}

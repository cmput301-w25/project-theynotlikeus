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
 * UI test for the sign up flow.
 * This test launches LoginActivity, navigates from the LoginUserSelectionFrag to the UserLoginFrag by clicking the "User" button,
 * then clicking the sign up button, and testing for successful sign up and sign up failure.
 */
@RunWith(AndroidJUnit4.class)
public class UserSignUpFragTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSignUpSuccess() {
        // Navigate from LoginUserSelectionFrag to UserSignUpFrag
        onView(withId(R.id.button_user)).perform(click());
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Input a new user in the correct manner.
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("newUser"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("newPassword"));
        onView(withId(R.id.editText_userSignUpFrag_reEnterPassword)).perform(replaceText("newPassword"));
        onView(withId(R.id.button_userSignUpFrag_createandlogin)).perform(click());

    }

    @Test
    public void testSignUpFailure() {
        // Navigate from LoginUserSelectionFrag to UserSignUpFrag.
        onView(withId(R.id.button_user)).perform(click());
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        // Input user with password mismatch.
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("newUser"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("newPassword1"));
        onView(withId(R.id.editText_userSignUpFrag_reEnterPassword)).perform(replaceText("newPassword2"));

        // Click on the sign-in button.
        onView(withId(R.id.button_userSignUpFrag_createandlogin)).perform(click());

        // Verify that the login fragment is still displayed by checking for a view with id "fragment_user_login_layout".
        onView(withId(R.id.fragment_user_sign_up_layout)).check(matches(isDisplayed()));
    }
}
package com.example.theynotlikeus;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.fragment.app.testing.FragmentScenario;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
Unit test case of the UserLoginFrag class.
Will test the UserLoginFrag
 */

@RunWith(AndroidJUnit4.class)
public class UserLoginFragTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(UserLoginFrag.class);
    }

    @Test
    public void testLoginSuccess() {
        //onView(withId(R.id.button_user)).perform(click());

        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("validUser"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("correctPassword"));
        onView(withId(R.id.button_UserLogIn_SignIn)).perform(click());

        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginFailure() {
        //onView(withId(R.id.button_user)).perform(click());

        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("wrongUsername"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("wrongPassword"));
        onView(withId(R.id.button_UserLogIn_SignIn)).perform(click());

        onView(withId(R.id.fragment_user_login_layout)).check(matches(isDisplayed()));

    }
}

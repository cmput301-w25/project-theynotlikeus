package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * UI test for the login flow.
 * This test launches LoginActivity, navigates from the LoginUserSelectionFrag to the UserLoginFrag by clicking the "User" button,
 * then verifies both successful and failed login attempts.
 */
@RunWith(AndroidJUnit4.class)
public class UserLoginFragTest {

    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        // "10.0.2.2" is the special IP address to access localhost from an Android emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Helper method to add a user to the database.
     */
    private void addUserToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "nour");
        user.put("password", "123");
        // Write the user data to the "users" collection with document id "nour".
        db.collection("users").document("nour").set(user);
    }

    @Test
    public void testLoginSuccess() throws InterruptedException {
        // Add user to the local database before logging in.
        addUserToDatabase();

        // Wait briefly to allow the user data to be added.
        Thread.sleep(2000);

        // 1. Navigate to the UserLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());

        // 2. Wait briefly for navigation to complete.
        Thread.sleep(2000);

        // 3. Enter the correct credentials and click the sign-in button.
        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("nour"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.button_UserLogin_SignIn)).perform(click());
    }

    @Test
    public void testLoginFailure() {
        // 1. Navigate to the UserLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());

        // 2. Enter incorrect credentials.
        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("wrongUsername"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("wrongPassword"));

        // 3. Click on the sign-in button.
        onView(withId(R.id.button_UserLogin_SignIn)).perform(click());

        // 4. Verify that the login fragment is still displayed.
        onView(withId(R.id.fragment_user_login_layout)).check(matches(isDisplayed()));
    }
}
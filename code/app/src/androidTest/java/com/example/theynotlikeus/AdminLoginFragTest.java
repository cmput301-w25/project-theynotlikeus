package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * UI test for the admin login flow.
 * This test launches LoginActivity, navigates from the LoginUserSelectionFrag to the AdminLoginFrag
 * by clicking the "Admin" button, then verifies both successful and failed login attempts.
 */
@RunWith(AndroidJUnit4.class)
public class AdminLoginFragTest {

    /**
     * Launch LoginActivity for testing.
     */
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Helper method to add an admin user to the local database.
     */
    private void addAdminToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> admin = new HashMap<>();
        admin.put("username", "admin");
        admin.put("password", "adminpass");
        // Write the admin data to the "admin" collection with document id "admin".
        db.collection("admin").document("admin").set(admin);
    }

    /**
     * Test: Verify that a successful admin login works.
     *
     * @throws InterruptedException
     */
    @Test
    public void testAdminLoginSuccess() throws InterruptedException {
        // Add admin to the local database before logging in.
        addAdminToDatabase();

        // Wait briefly for the admin data to be added (consider using an Espresso IdlingResource for production tests).
        Thread.sleep(3000);

        // 1. Navigate to the AdminLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_admin))
                .check(matches(isDisplayed()))
                .perform(click());

        // 2. Wait for the admin login fragment to appear.
        onView(withId(R.id.editText_adminLoginFrag_username))
                .check(matches(isDisplayed()));

        // 3. Enter the correct admin credentials.
        onView(withId(R.id.editText_adminLoginFrag_username)).perform(replaceText("admin"));
        onView(withId(R.id.editText_adminLoginFrag_password)).perform(replaceText("adminpass"));

        // Optional: wait a bit to allow any UI state changes.
        Thread.sleep(2000);

        // 4. Ensure the sign-in button is visible and enabled, then click it.
        onView(withId(R.id.button_adminLogin_SignIn))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());
    }

    /**
     * Test: Verify that an admin login attempt fails with incorrect credentials.
     *
     * @throws InterruptedException
     */
    @Test
    public void testAdminLoginFailure() throws InterruptedException {
        // 1. Navigate to the AdminLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_admin))
                .check(matches(isDisplayed()))
                .perform(click());

        // 2. Wait for the admin login fragment to appear.
        onView(withId(R.id.editText_adminLoginFrag_username))
                .check(matches(isDisplayed()));

        // 3. Enter incorrect admin credentials.
        onView(withId(R.id.editText_adminLoginFrag_username)).perform(replaceText("wrongAdmin"));
        onView(withId(R.id.editText_adminLoginFrag_password)).perform(replaceText("wrongPass"));

        // Optional: wait a bit to allow UI updates.
        Thread.sleep(2000);

        // 4. Ensure the sign-in button is visible and enabled, then click it.
        onView(withId(R.id.button_adminLogin_SignIn))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        // 5. Verify that the admin login fragment is still displayed (indicating login failure).
        onView(withId(R.id.ImageView_LoginUserSelection_appTitle))
                .check(matches(isDisplayed()));
    }
}

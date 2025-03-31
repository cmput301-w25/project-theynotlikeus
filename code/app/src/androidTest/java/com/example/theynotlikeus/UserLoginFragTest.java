package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    /**
     * Helper method to add a user to the database and wait for confirmation.
     */
    private void addUserToDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "nour");
        user.put("password", "123");

        CountDownLatch latch = new CountDownLatch(1);
        db.collection("users").document("nour").set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Test user added");
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add test user", e);
                    latch.countDown();
                });

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out while adding test user.");
        }

        // Optional: Verify document exists
        CountDownLatch verifyLatch = new CountDownLatch(1);
        db.collection("users").document("nour").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Log.i("Firestore", "User verified in Firestore");
                    } else {
                        Log.e("Firestore", "User document not found");
                    }
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Verification failed", e);
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("Firestore verification timed out.");
        }
    }

    /**
     * Test: If login is successful.
     */
    @Test
    public void testLoginSuccess() throws InterruptedException {
        addUserToDatabase(); // Ensure test user is in Firestore

        // Navigate to UserLoginFrag
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(1000); // Small delay for fragment transition

        // Enter correct credentials
        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("nour"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.button_UserLogin_SignIn)).perform(click());

        // (Optional) Add a check here to confirm navigation away from login screen
    }

    /**
     * Test: If login fails with incorrect credentials.
     */
    @Test
    public void testLoginFailure() {
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());

        onView(withId(R.id.editText_userLoginFrag_username)).perform(replaceText("wrongUsername"));
        onView(withId(R.id.editText_userLoginFrag_password)).perform(replaceText("wrongPassword"));
        onView(withId(R.id.button_UserLogin_SignIn)).perform(click());

        // Confirm login fragment is still visible (login failed)
        onView(withId(R.id.fragment_user_login_layout)).check(matches(isDisplayed()));
    }
}

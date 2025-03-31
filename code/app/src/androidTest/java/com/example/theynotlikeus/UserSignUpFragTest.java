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
import androidx.test.filters.LargeTest;

import com.example.theynotlikeus.view.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * UI Test: UserSignUpFrag
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserSignUpFragTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Helper method to add a user to the Firestore emulator.
     */
    private void addUserToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "nour");
        user.put("password", "123");
        db.collection("users").document("nour").set(user);
    }

    /**
     * Test: Successful sign up with a unique username.
     */
    @Test
    public void testSignUpSuccess() throws InterruptedException {
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        String uniqueUsername = "user" + System.currentTimeMillis();
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText(uniqueUsername));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("123"));
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());
    }

    /**
     * Test: Failed sign up due to mismatched passwords.
     */
    @Test
    public void testSignUpFailure() throws InterruptedException {
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("newUser"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("newPassword1"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("newPassword2"));
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());

        onView(withId(R.id.fragment_user_sign_up_layout)).check(matches(isDisplayed()));
    }

    /**
     * Test: Failed sign up due to already existing user.
     */
    @Test
    public void testExistingUserFailure() throws InterruptedException {
        addUserToDatabase();

        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("nour"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("123"));
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());

        onView(withId(R.id.fragment_user_sign_up_layout)).check(matches(isDisplayed()));
    }

    /**
     * Clear all emulator data after each test to avoid test pollution.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        try {
            URL url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId +
                    "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("TearDown", "Emulator DB cleared with response code: " + response);
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.e("TearDown", "Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TearDown", "IO Exception: " + e.getMessage());
        }
    }
}

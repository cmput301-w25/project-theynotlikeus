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
import org.junit.BeforeClass;
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

    /**
     * Set scenario to be MainActivity
     */
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Configure Firestore to use the local emulator.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

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

    /**
     * Test: if Sign Up is successful
     * @throws InterruptedException
     */
    @Test
    public void testSignUpSuccess() throws InterruptedException {
        // 1. Navigate from LoginUserSelectionFrag to UserSignUpFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());

        // 2. Wait for navigation to complete.
        Thread.sleep(2000);

        // 3. In UserLoginFrag, click the sign-up button.
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        // 4. Generate a unique username and enter sign-up credentials.
        String uniqueUsername = "user" + System.currentTimeMillis();
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText(uniqueUsername));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("123"));
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());
    }


    /**
     * Test: if sign up fails
     * @throws InterruptedException
     */
    @Test
    public void testSignUpFailure() throws InterruptedException {
        // 1. Navigate from LoginUserSelectionFrag to UserSignUpFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        // 2. Enter credentials with a password mismatch.
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("newUser"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("newPassword1"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("newPassword2"));

        // 3. Attempt to create an account.
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());

        // 4. Verify that the sign-up fragment remains displayed (indicating failure).
        onView(withId(R.id.fragment_user_sign_up_layout)).check(matches(isDisplayed()));
    }

    /**
     * Tests: if sign up fails due to having an already existing user.
     * @throws InterruptedException
     */
    @Test
    public void testExistingUserFailure() throws InterruptedException {
        // Add user to database
        addUserToDatabase();

        // 1. Navigate from LoginUserSelectionFrag to UserSignUpFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_user)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.textButton_UserLoginFrag_signUp)).perform(click());

        // 2. Enter credentials with an already existing user.
        onView(withId(R.id.editText_UserSignUpFrag_username)).perform(replaceText("nour"));
        onView(withId(R.id.editText_UserSignUpFrag_password)).perform(replaceText("123"));
        onView(withId(R.id.editText_UserSignUpFrag_reEnterPassword)).perform(replaceText("123"));

        // 3. Attempt to create an account.
        onView(withId(R.id.button_UserSignUpFrag_createandlogin)).perform(click());

        // 4. Verify that the sign up fragment is still displayed.
        onView(withId(R.id.fragment_user_sign_up_layout)).check(matches(isDisplayed()));
    }

    /**
     * Clear all documents from the emulator after each test.
     */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId +
                    "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
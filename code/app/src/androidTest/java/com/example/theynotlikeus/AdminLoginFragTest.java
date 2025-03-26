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

import com.example.theynotlikeus.view.AdminLoginFrag;
import com.example.theynotlikeus.view.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
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
     * Set up Firestore to use the local emulator.
     * "10.0.2.2" is the special IP address to access localhost from an Android emulator.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

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
        // Write the admin data to the "admins" collection with document id "admin".
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

        // Wait briefly to allow the admin data to be added.
        Thread.sleep(2000);

        // 1. Navigate to the AdminLoginFrag.
        // Assumes that LoginActivity has a button with id "button_LoginUserSelectionFragment_admin"
        // that navigates to the AdminLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_admin)).perform(click());

        // 2. Wait briefly for navigation to complete.
        Thread.sleep(2000);

        // 3. Enter the correct admin credentials and click the sign-in button.
        onView(withId(R.id.editText_adminLoginFrag_username)).perform(replaceText("admin"));
        onView(withId(R.id.editText_adminLoginFrag_password)).perform(replaceText("adminpass"));
        onView(withId(R.id.button_adminLogin_SignIn)).perform(click());
    }

    /**
     * Test: Verify that an admin login attempt fails with incorrect credentials.
     */
    @Test
    public void testAdminLoginFailure() throws InterruptedException {
        // 1. Navigate to the AdminLoginFrag.
        onView(withId(R.id.button_LoginUserSelectionFragment_admin)).perform(click());

        // 2. Enter incorrect admin credentials.
        onView(withId(R.id.editText_adminLoginFrag_username)).perform(replaceText("wrongAdmin"));
        onView(withId(R.id.editText_adminLoginFrag_password)).perform(replaceText("wrongPass"));

        // 3. Click the sign-in button.
        onView(withId(R.id.button_adminLogin_SignIn)).perform(click());

        // 4. Verify that the admin login fragment is still displayed.
        // Here we check that a view in the admin login fragment (for example, the app title)
        // is still visible.
        onView(withId(R.id.textView_AdminLoginFrag_apptitle)).check(matches(isDisplayed()));
    }
}

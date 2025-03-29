package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MoodEventDetailsActivity;
import com.example.theynotlikeus.view.ViewUserProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * UI test for Viewing another users profile
 * and testing for requesting a follow.
 */
@RunWith(AndroidJUnit4.class)
public class ViewUserProfileActivityTest {

    @Rule
    public ActivityScenarioRule<ViewUserProfileActivity> scenario = new ActivityScenarioRule<>(ViewUserProfileActivity.class);

    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    /**
     * Adds user to database to test after
     * @param onComplete
     */
    private void addUserToDatabase(Runnable onComplete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testUser");
        user.put("password", "123");

        db.collection("users").document("testUser").set(user)
                .addOnSuccessListener(aVoid -> {
                    onComplete.run();  // Proceed only when Firestore write is confirmed
                });
    }

    @Test
    public void testSeePerson() throws InterruptedException {
        addUserToDatabase(() -> {
            Intent intent = new Intent(
                    ApplicationProvider.getApplicationContext(),
                    ViewUserProfileActivity.class
            );
            try (ActivityScenario<ViewUserProfileActivity> scenario = ActivityScenario.launch(intent)) {
                onView(withId(R.id.textView_fragmentUserFollowed_Username))
                        .check(matches(withText("testUser")));
            }
        });
    }

    @Test
    public void testFollowRequestAndUserVisibility() throws InterruptedException {
        addUserToDatabase(() -> {

            // Click the follow request button
            onView(withId(R.id.button_fragmentUserFollowed_follow)).perform(click());

            // Wait for UI update
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Verify follow request was sent
            onView(withId(R.id.button_fragmentUserFollowed_follow))
                    .check((view, noViewFoundException) -> {
                        if (noViewFoundException != null) throw noViewFoundException;
                        Button button = (Button) view;
                        assertEquals("", button.getText().toString()); // Button works, checks that button works
                    });

        });
    }
}
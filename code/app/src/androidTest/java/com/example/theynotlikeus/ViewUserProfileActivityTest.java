package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.ViewUserProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI test for viewing another user's profile and testing follow request functionality.
 */
@RunWith(AndroidJUnit4.class)
public class ViewUserProfileActivityTest {

    /**
     * Adds a test user to Firestore and waits for confirmation.
     */
    private void addUserToDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testUser");
        user.put("password", "123");

        CountDownLatch latch = new CountDownLatch(1);
        db.collection("users").document("testUser").set(user)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Firestore write timed out");
        }
    }

    /**
     * Test: See test user's profile and verify username is displayed.
     */
    @Test
    public void testSeePerson() throws InterruptedException {
        addUserToDatabase();

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                ViewUserProfileActivity.class
        );
        intent.putExtra("username", "testUser");

        try (ActivityScenario<ViewUserProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.textView_fragmentUserFollowed_Username))
                    .check(matches(withText("testUser")));
        }
    }

    /**
     * Test: Send follow request to test user and check UI updates.
     */
    @Test
    public void testFollowRequestAndUserVisibility() throws InterruptedException {
        addUserToDatabase();

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                ViewUserProfileActivity.class
        );
        intent.putExtra("username", "testUser");

        try (ActivityScenario<ViewUserProfileActivity> scenario = ActivityScenario.launch(intent)) {

            onView(withId(R.id.button_fragmentUserFollowed_follow)).perform(click());

            // Wait briefly for UI state to update
            Thread.sleep(1500);

            // Verify the follow button updates
            onView(withId(R.id.button_fragmentUserFollowed_follow))
                    .check((view, noViewFoundException) -> {
                        if (noViewFoundException != null) throw noViewFoundException;
                        Button button = (Button) view;
                        // Check button label/text is changed (e.g., empty or "Requested")
                        assertEquals("", button.getText().toString().trim());
                    });
        }
    }
}

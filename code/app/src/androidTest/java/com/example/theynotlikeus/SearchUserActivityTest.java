package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.theynotlikeus.view.SearchUserActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchUserActivityTest {

    @Rule
    public ActivityScenarioRule<SearchUserActivity> scenario =
            new ActivityScenarioRule<>(SearchUserActivity.class);

    /**
     * Adds a test user to Firestore and waits until it's written and verified.
     */
    private void addUserToDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testUser");
        user.put("password", "123");

        CountDownLatch latch = new CountDownLatch(1);

        db.collection("users").document("testUser").set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Test user added");
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add test user", e);
                    latch.countDown();
                });

        if (!latch.await(15, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out while adding test user.");
        }

        CountDownLatch verifyLatch = new CountDownLatch(1);
        db.collection("users").document("testUser").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.i("Firestore", "Test user successfully verified in Firestore.");
                    } else {
                        Log.e("Firestore", "Test user not found after write.");
                    }
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to verify test user.", e);
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Firestore verification timed out!");
        }
    }

    @Test
    public void testSearchFunctionality() throws InterruptedException {
        addUserToDatabase(); // Ensure Firestore is ready

        // Type in search bar
        onView(withId(R.id.search_edit_text))
                .perform(typeText("testUser"), closeSoftKeyboard());

        // Check if user appears in RecyclerView
        onView(withId(R.id.recyclerview_SearchUserActivity))
                .check(matches(hasDescendant(withText("testUser"))));
    }
}

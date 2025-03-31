package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.view.AddMoodEventActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class OfflineMoodInstrumentedTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    private FirebaseFirestore db;
    private MoodController moodController;
    private String testUsername = "offlineTestUser";
    private String testTriggerText = "Offline sync test trigger";

    @Before
    public void setup() {
        db = FirebaseFirestore.getInstance();
        moodController = new MoodController();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Test
    public void testOfflineMoodSyncToOnline() throws InterruptedException {
        // Ensure offline mode at the start
        assertTrue("Ensure you're offline before running this test.", !isNetworkAvailable());

        ActivityScenario<AddMoodEventActivity> scenario = ActivityScenario.launch(AddMoodEventActivity.class);

        // Fill in form fields for mood submission
        onView(withId(R.id.spinner_ActivityAddMoodEvent_currentMoodspinner)).perform(click());
        onView(withText("Happiness")).perform(click());

        onView(withId(R.id.editText_ActivityAddMoodEvent_triggerInput))
                .perform(typeText(testTriggerText), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_ActivityAddMoodEvent_save)).perform(click());

        // Wait to allow local Firestore write
        SystemClock.sleep(4000);

        // Now simulate coming back online and waiting for sync
        int maxWaitTime = 15000; // max 15 seconds
        int interval = 3000; // check every 3 seconds
        AtomicBoolean synced = new AtomicBoolean(false);

        for (int waited = 0; waited < maxWaitTime; waited += interval) {
            if (isNetworkAvailable()) {
                CountDownLatch latch = new CountDownLatch(1);
                moodController.getMoodsByUser(testUsername, moods -> {
                    for (Mood mood : moods) {
                        if (testTriggerText.equals(mood.getTrigger())) {
                            synced.set(true);
                            break;
                        }
                    }
                    latch.countDown();
                }, error -> latch.countDown());
                latch.await(5, TimeUnit.SECONDS);

                if (synced.get()) break;
            }
            SystemClock.sleep(interval);
        }

        assertTrue("Mood did not sync after returning online.", synced.get());
    }

    @After
    public void cleanup() {
        // Optionally delete test data from Firestore
    }
}
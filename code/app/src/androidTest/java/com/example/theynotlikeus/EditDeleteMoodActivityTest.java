package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class EditDeleteMoodActivityTest {

    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Helper method to add a mood to the database.
     * @return the document ID of the inserted mood.
     */
    private String addMoodToDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mood = new HashMap<>();
        // Set fields corresponding to the Mood object.
        mood.put("moodState", "HAPPY");
        mood.put("trigger", "Test trigger");
        mood.put("socialSituation", "ALONE");

        CountDownLatch latch = new CountDownLatch(1);
        final String[] moodId = new String[1];
        final boolean[] success = new boolean[]{false};

        db.collection("moods")
                .add(mood)
                .addOnSuccessListener(documentReference -> {
                    moodId[0] = documentReference.getId();
                    success[0] = true;
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    success[0] = false;
                    latch.countDown();
                });
        // Increase timeout to 10 seconds to allow for potential emulator delays.
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new InterruptedException("Firestore write timed out");
        }
        if (!success[0]) {
            fail("Failed to add mood to Firestore.");
        }
        return moodId[0];
    }

    @Test
    public void testEditDeleteMoodActivityDisplaysMoodData() throws InterruptedException {
        // 1. Add a test mood to Firestore.
        String moodId = addMoodToDatabase();

        // Wait briefly to ensure data is available.
        Thread.sleep(2000);

        // 2. Launch EditDeleteMoodActivity with the moodId extra.
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                EditDeleteMoodActivity.class
        );
        intent.putExtra("moodId", moodId);

        try (ActivityScenario<EditDeleteMoodActivity> scenario = ActivityScenario.launch(intent)) {
            // 3. Verify that the UI is populated with the correct mood data.
            onView(withId(R.id.editText_DeleteEditMoodActivity_triggerInput))
                    .check(matches(withText("Test trigger")));

            onView(withId(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner))
                    .check(matches(withSpinnerText(containsString("Happy"))));

            onView(withId(R.id.spinner_DeleteEditMoodActivity_socialsituation))
                    .check(matches(withSpinnerText(containsString("Alone"))));

            onView(withId(R.id.switch_DeleteEditMoodActivity_geoSwitch))
                    .check(matches(isNotChecked()));
        }
    }
}

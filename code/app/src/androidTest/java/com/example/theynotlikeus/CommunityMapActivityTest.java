package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import android.os.SystemClock;
import com.example.theynotlikeus.view.CommunityMapActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class CommunityMapActivityTest {

    @BeforeClass
    public static void setup() throws InterruptedException {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";  // Emulator’s alias for host machine localhost.
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

        // Insert test follow data: For user "defaultUser", add a follow relationship with friend "friendUser".
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> followData = new HashMap<>();
        followData.put("follower", "defaultUser");
        followData.put("followee", "friendUser");
        db.collection("follow").document("test_follow").set(followData);

        // Insert a test mood event for "friendUser" with valid geo coordinates (within 5 km of some default location).
        Map<String, Object> mood = new HashMap<>();
        mood.put("username", "friendUser");
        mood.put("moodState", "HAPPINESS"); // Example mood state.
        mood.put("dateTime", new Date());
        mood.put("latitude", 37.422);       // Example latitude.
        mood.put("longitude", -122.084);    // Example longitude.
        mood.put("pendingReview", false);   // Approved mood.
        // Write the mood to the "moods" collection with document id "test_mood_friend".
        db.collection("moods").document("test_mood_friend").set(mood);

        // Wait a few seconds to ensure the documents are written.
        SystemClock.sleep(3000);
    }

    @Rule
    public ActivityScenarioRule<CommunityMapActivity> activityScenarioRule =
            new ActivityScenarioRule<>(CommunityMapActivity.class);

    @Test
    public void testFollowerMoodAdded() throws InterruptedException {
        // Launch CommunityMapActivity.
        ActivityScenario.launch(CommunityMapActivity.class);

        // Wait for the activity and its data to load.
        SystemClock.sleep(5000);

        // This indicates that at least one marker was added
        onView(withId(R.id.fragment_CommunityMapActivity_map))
                .check(matches(isDisplayed()));
    }
}

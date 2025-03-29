package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.model.Mood.MoodState;
import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class FilterTriggerTextCatTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void setupEmulator() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator(androidLocalhost, portNumber);

        Mood catMood = new Mood();
        catMood.setUsername("testuser");
        catMood.setMoodState(MoodState.SURPRISE);
        catMood.setTrigger("cat knocked");
        catMood.setDateTime(new Date());
        catMood.setPendingReview(false);
        catMood.setPublic(true);

        db.collection("moods").document("cat_trigger_test_mood").set(catMood)
                .addOnSuccessListener(unused -> Log.d("TestSetup", "Cat mood added"))
                .addOnFailureListener(e -> Log.e("TestSetup", "Failed to add cat mood", e));
    }

    @Test
    public void testFilterByTriggerText_Cat() throws InterruptedException {
        onView(withId(R.id.search_edit_text)).perform(typeText("cat"));
        androidx.test.espresso.Espresso.closeSoftKeyboard();
        Thread.sleep(2000);
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));
    }
}

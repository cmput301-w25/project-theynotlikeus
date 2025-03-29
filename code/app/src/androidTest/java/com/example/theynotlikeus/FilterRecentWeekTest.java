package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class FilterRecentWeekTest {

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
        String emulatorHost = "10.0.2.2";
        int port = 8089;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator(emulatorHost, port);

        // Add a test mood from within the last 7 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2); // 2 days ago

        Mood recentMood = new Mood();
        recentMood.setUsername("testuser");
        recentMood.setMoodState(MoodState.SADNESS);
        recentMood.setTrigger("exam stress");
        recentMood.setDateTime(calendar.getTime());
        recentMood.setPendingReview(false);
        recentMood.setPublic(true);
        // log for testing


        db.collection("moods").document("recent_week_test_mood").set(recentMood)
                .addOnSuccessListener(aVoid -> Log.d("TestSetup", "Recent week test mood added"))
                .addOnFailureListener(e -> Log.e("TestSetup", "Failed to add recent week mood", e));
    }





    @Test
    public void testFilterByRecentWeek() throws InterruptedException {
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));
    }


}

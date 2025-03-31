package com.example.theynotlikeus;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.model.Mood.MoodState;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FilterRecentWeekTest {

    @BeforeClass
    public static void setupEmulator() {
        // Removed the call to useEmulator() since it's already configured in CustomTestRunner.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Create a recent mood (1 day ago).

        // Create and upload a Mood object.
        Mood mood = new Mood();
        mood.setUsername("testuser");
        mood.setMoodState(MoodState.SADNESS);
        mood.setTrigger("exam stress");
        mood.setDateTime(calendar.getTime());
        mood.setPendingReview(false);
        mood.setPublic(true);

        db.collection("moods").document("recent_test").set(mood);
    }

    @Test
    public void testRecentWeekFilter() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testuser");
        intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        Thread.sleep(2000);
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)).check(matches(isDisplayed()));
    }
}

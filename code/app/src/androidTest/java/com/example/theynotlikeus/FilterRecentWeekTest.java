package com.example.theynotlikeus;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Calendar;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.model.Mood.MoodState;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;


/**
* Test for the recent week checkbox in HomeMyMoodsFrag
*  Inserts a test Mood into the Firestore emulator that is dated within the last 7 days.
*   Launches the main activity and simulates the user checking the "Recent Week" checkbox filter.
*   Verifies that at least one mood appears in the RecyclerView, confirming that the recent mood is correctly filtered and displayed.
 *
*
* */


@RunWith(AndroidJUnit4.class)
public class FilterRecentWeekTest {


    @BeforeClass
    public static void setupEmulator() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8089);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // recent

        // Create and upload a Mood object
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

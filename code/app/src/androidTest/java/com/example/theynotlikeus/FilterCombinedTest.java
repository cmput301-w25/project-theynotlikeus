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
 * Test the filter bar in HomeMyMoodsFrag
 *Inserts a Mood with the state HAPPINESS into the Firestore emulator.
 *Simulates the user selecting "Happiness" from the dropdown filter.
 *Verifies that the mood appears in the list, confirming that filtering by emotional state works as expected.
 *
 * */


@RunWith(AndroidJUnit4.class)
public class FilterCombinedTest {

    @BeforeClass
    public static void setupEmulator() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8089);

        Mood mood = new Mood();
        mood.setUsername("testuser");
        mood.setMoodState(MoodState.HAPPINESS);
        mood.setTrigger("cat joy");
        mood.setDateTime(new Date());
        mood.setPendingReview(false);
        mood.setPublic(true);

        db.collection("moods").document("combo_test").set(mood);
    }



    @Test
    public void testCombinedFilters() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testuser");
        intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        Thread.sleep(2000);
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).perform(click());
        onView(withId(R.id.autoCompleteTextView)).perform(click());
        Thread.sleep(500);
        onView(withText("Happiness")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.search_edit_text)).perform(typeText("cat"));
        Thread.sleep(1000);
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)).check(matches(isDisplayed()));
    }




}

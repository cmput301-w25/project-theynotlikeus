package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.espresso.matcher.RootMatchers;
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
public class FilterCombinedTest {

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

        // Create and upload a Mood object
        Mood testMood = new Mood();
        testMood.setUsername("testuser");
        testMood.setMoodState(MoodState.HAPPINESS);
        testMood.setTrigger("my cat wrote test cases");
        testMood.setDateTime(new Date());
        testMood.setPendingReview(false);
        testMood.setPublic(true);

        // log for testing
        db.collection("moods").document("filter_test_mood").set(testMood)
                .addOnSuccessListener(unused -> Log.d("TestSetup", "Test mood added"))
                .addOnFailureListener(e -> Log.e("TestSetup", "Failed to add test mood", e));
    }




    @Test
    public void testCombinedFilters() throws InterruptedException {
        onView(withId(R.id.checkBox_HomeMyMoodsFragment_recentWeek)).perform(click());
        onView(withId(R.id.autoCompleteTextView)).perform(click());
        onView(withId(R.id.autoCompleteTextView)).perform(click());
        Thread.sleep(500); // Give time for dropdown to render
        // Important! Only this works for material UI
        onView(withText("Happiness"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_edit_text)).perform(typeText("cat"));
        androidx.test.espresso.Espresso.closeSoftKeyboard();
        Thread.sleep(2000);
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));
    }


}
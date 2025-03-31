package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CommentOnMoodEventTest {

    @Test
    public void testAbleToCommentOnMoodEvent() {
        // Generate a unique username using the current time.
        String uniqueUsername = "testUser_" + System.currentTimeMillis();

        // Create a test Mood event with the unique username.
        Mood testMood = new Mood();
        testMood.setUsername(uniqueUsername);
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId("testMoodId"); // You can also make this unique if desired.

        // Set the logged-in username in your app's singleton.
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(uniqueUsername);

        // Launch the FriendMoodEventDetailsActivity with the test Mood and unique username.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", uniqueUsername);
        ActivityScenario.launch(intent);

        // Tap the "Comments" button.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Verify that the comments RecyclerView is displayed.
        onView(withId(R.id.recyclerview_ViewCommentsActivity_commentsRecyclerView))
                .check(matches(isDisplayed()));
    }
}
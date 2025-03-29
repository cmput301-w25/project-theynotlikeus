package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testAddCommentToMoodEvent() throws InterruptedException {
        // Generate a unique username.
        String uniqueUsername = "testUser_" + System.currentTimeMillis();

        // Create a test Mood event with the unique username.
        Mood testMood = new Mood();
        testMood.setUsername(uniqueUsername);
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId("testMoodId");

        // Set the logged-in username.
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(uniqueUsername);

        // Launch the FriendMoodEventDetailsActivity with the test Mood.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", uniqueUsername);
        ActivityScenario.launch(intent);

        // Tap the "Comments" button to navigate to the comments screen.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Tap the "add comment" button in the comments screen.
        onView(withId(R.id.imagebutton_ActivityViewComments_addcommentsbutton)).perform(click());

        // Create a unique comment using current time.
        String testComment = "This is a test comment from Espresso " + System.currentTimeMillis();

        // In the AddCommentDialogFrag, type the comment and tap "Post".
        onView(withId(R.id.edittext_FragmentAddCommentsDialogFrag_editCommentText))
                .perform(replaceText(testComment), closeSoftKeyboard());
        onView(withText("Post")).perform(click());

        // Verify that the new comment appears in the UI.
        onView(withText(testComment)).check(matches(isDisplayed()));

        // Now verify that the comment was saved in Firestore's "comments" collection.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] commentSaved = { false };

        // Assuming the Comment object is saved with a field "commentText".
        db.collection("comments")
                .whereEqualTo("commentText", testComment)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        commentSaved[0] = true;
                    }
                    latch.countDown();
                });

        // Wait up to 5 seconds for the Firestore query to complete.
        latch.await(5, TimeUnit.SECONDS);

        // Assert that the comment exists in Firestore.
        assertTrue("The comment was not saved in Firestore", commentSaved[0]);
    }
}

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
        // Create a test Mood event.
        Mood testMood = new Mood();
        testMood.setUsername("friend1");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId("testMoodId"); // assuming Mood has a setter for the document ID

        // Set the logged-in username.
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername("testUser");

        // Launch FriendMoodEventDetailsActivity with the test Mood.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", "testUser");

        ActivityScenario.launch(intent);

        // Tap the "Comments" button.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Verify that the comments RecyclerView is displayed.
        onView(withId(R.id.recyclerview_ViewCommentsActivity_commentsRecyclerView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddCommentToMoodEvent() throws InterruptedException {
        // Create a test Mood event.
        Mood testMood = new Mood();
        testMood.setUsername("friend1");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId("testMoodId");

        // Set the logged-in username.
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername("testUser");

        // Launch FriendMoodEventDetailsActivity with the test Mood.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", "testUser");

        ActivityScenario.launch(intent);

        // Tap the "Comments" button to navigate to ViewCommentsActivity.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Tap the "add comment" button in ViewCommentsActivity.
        onView(withId(R.id.imagebutton_ActivityViewComments_addcommentsbutton)).perform(click());

        // In the displayed AddCommentDialogFrag, type a test comment.
        String testComment = "This is a test comment from Espresso";
        onView(withId(R.id.edittext_FragmentAddCommentsDialogFrag_editCommentText))
                .perform(replaceText(testComment), closeSoftKeyboard());

        // Tap the "Post" button in the dialog.
        onView(withText("Post")).perform(click());

        // Verify that the new comment appears in the RecyclerView.
        onView(withText(testComment)).check(matches(isDisplayed()));

        // Now verify the comment was saved in Firestore's "comments" collection.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] commentSaved = { false };

        // Assuming that the Comment object is saved with a field "commentText".
        db.collection("comments")
                .whereEqualTo("commentText", testComment)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        commentSaved[0] = true;
                    }
                    latch.countDown();
                });

        // Wait up to 5 seconds for the query to complete.
        latch.await(5, TimeUnit.SECONDS);

        // Assert that the comment exists in the database.
        assertTrue("The comment was not saved in Firestore", commentSaved[0]);
    }
}

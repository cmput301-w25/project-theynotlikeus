package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.model.Comment;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test case to verify that all comments for a given mood event are displayed.
 */
@RunWith(AndroidJUnit4.class)
public class ViewAllCommentsTest {

    private FirebaseFirestore db;

    /**
     * Clears the "comments" collection before each test.
     */
    @Before
    public void clearCommentsCollection() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        db.collection("comments")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        CountDownLatch deleteLatch = new CountDownLatch(querySnapshot.size());
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete().addOnCompleteListener(task -> deleteLatch.countDown());
                        }
                        try {
                            deleteLatch.await(5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Log.e("Firestore", "Delete interrupted", e);
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testViewAllCommentsForGivenMoodEvent() throws InterruptedException {
        db = FirebaseFirestore.getInstance();

        // 1. Create a unique mood event.
        String moodDocId = "testMoodViewAll_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("friend1");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        // 2. Pre-populate Firestore with comments associated with the mood.
        int numComments = 3;
        String[] commentTexts = new String[numComments];
        CountDownLatch insertLatch = new CountDownLatch(numComments);

        for (int i = 0; i < numComments; i++) {
            commentTexts[i] = "Comment " + (i + 1) + " for mood " + moodDocId;
            Comment comment = new Comment(commentTexts[i], new Date());
            comment.setCommentDateTime(); // Set timestamp
            comment.setAssociatedMoodID(moodDocId);

            db.collection("comments")
                    .document("comment_" + i + "_" + System.currentTimeMillis())
                    .set(comment)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("Firestore", "Failed to add comment");
                        }
                        insertLatch.countDown();
                    });
        }

        if (!insertLatch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Timeout while inserting comments");
        }

        // 3. Set up fake logged-in user.
        String uniqueUsername = "testUser_" + System.currentTimeMillis();
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(uniqueUsername);

        // 4. Launch activity with the mood and username.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", uniqueUsername);
        ActivityScenario.launch(intent);

        // 5. Navigate to Comments screen.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Optional wait to ensure UI loads
        Thread.sleep(1500);

        // 6. Verify each comment appears in the RecyclerView.
        for (String commentText : commentTexts) {
            onView(allOf(withText(commentText),
                    isDisplayed()))
                    .check(matches(isDisplayed()));
        }
    }
}

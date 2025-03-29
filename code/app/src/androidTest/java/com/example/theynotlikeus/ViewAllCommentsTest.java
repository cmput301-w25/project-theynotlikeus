package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.model.Comment;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setupEmulator() {
        // Configure Firestore to use the local emulator.
        // "10.0.2.2" is the special IP address to access localhost from an Android emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

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
                            // Handle if needed.
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testViewAllCommentsForGivenMoodEvent() throws InterruptedException {
        // 1. Create a unique mood event.
        String moodDocId = "testMoodViewAll_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("friend1");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("happy event");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        // 2. Pre-populate Firestore with multiple comments associated with this mood event.
        int numComments = 3;
        String[] commentTexts = new String[numComments];
        CountDownLatch insertLatch = new CountDownLatch(numComments);
        for (int i = 0; i < numComments; i++) {
            commentTexts[i] = "Comment " + (i + 1) + " for mood " + moodDocId;
            Comment comment = new Comment(commentTexts[i], new Date());
            // Explicitly set the comment date so it is not null.
            comment.setCommentDateTime();
            // Associate this comment with our test mood event.
            comment.setAssociatedMoodID(moodDocId);
            db.collection("comments").add(comment).addOnCompleteListener(task -> {
                insertLatch.countDown();
            });
        }
        // Wait for all comments to be inserted.
        insertLatch.await(5, TimeUnit.SECONDS);

        // 3. Create a unique logged-in username.
        String uniqueUsername = "testUser_" + System.currentTimeMillis();
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(uniqueUsername);

        // 4. Launch FriendMoodEventDetailsActivity with the test mood event.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", uniqueUsername);
        ActivityScenario.launch(intent);

        // 5. Tap the "Comments" button to navigate to the comments screen.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // 6. Verify that each pre-populated comment is displayed.
        for (String commentText : commentTexts) {
            // Check that the comment text is a descendant of the RecyclerView.
            onView(allOf(withText(commentText),
                    ViewMatchers.isDescendantOfA(withId(R.id.recyclerview_ViewCommentsActivity_commentsRecyclerView))))
                    .check(matches(isDisplayed()));
        }
    }
}

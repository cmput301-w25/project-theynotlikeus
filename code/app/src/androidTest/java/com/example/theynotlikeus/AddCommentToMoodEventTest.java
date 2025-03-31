package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AddCommentToMoodEventTest {

    private FirebaseFirestore db;

    @Before
    public void setup() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        // Clear the "comments" collection before each test.
        CountDownLatch latch = new CountDownLatch(1);
        db.collection("comments").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                doc.getReference().delete();
            }
            latch.countDown();
        }).addOnFailureListener(e -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
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
        // Create a unique document ID for the mood.
        testMood.setDocId("testMoodId_" + System.currentTimeMillis());

        // Set the logged-in username using the singleton.
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(uniqueUsername);

        // Launch FriendMoodEventDetailsActivity with the test Mood.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", uniqueUsername);
        ActivityScenario.launch(intent);

        // Tap the "Comments" button to navigate to the comments screen.
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        // Tap the "add comment" button in the comments screen.
        onView(withId(R.id.imagebutton_ActivityViewComments_addcommentsbutton)).perform(click());

        // Wait for the dialog to appear.
        Thread.sleep(500);

        // Create a unique comment using the current time.
        String testComment = "This is a test comment from Espresso " + System.currentTimeMillis();

        // In the Add Comment dialog, type the comment and tap "Post".
        onView(withId(R.id.edittext_FragmentAddCommentsDialogFrag_editCommentText))
                .inRoot(isDialog())
                .perform(replaceText(testComment), closeSoftKeyboard());
        onView(withText("Post"))
                .inRoot(isDialog())
                .perform(click());

        // Wait for Firebase to save the comment and for the UI to update.
        Thread.sleep(5000);

        // Verify that the new comment appears in the UI.
        onView(withText(testComment)).check(matches(isDisplayed()));

        // Now verify that the comment was saved in Firestore's "comments" collection.
        CountDownLatch firestoreLatch = new CountDownLatch(1);
        final boolean[] commentSaved = { false };

        db.collection("comments")
                .whereEqualTo("commentText", testComment)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() &&
                            task.getResult() != null &&
                            !task.getResult().isEmpty()) {
                        commentSaved[0] = true;
                    }
                    firestoreLatch.countDown();
                });
        firestoreLatch.await(5, TimeUnit.SECONDS);
        assertTrue("The comment was not saved in Firestore", commentSaved[0]);
    }
}

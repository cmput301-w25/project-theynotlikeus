package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
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

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.model.Comment;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumental test case to verify adding a comment via UI interaction.
 */
@RunWith(AndroidJUnit4.class)
public class AddCommentInstrumentedTest {

    private FirebaseFirestore db;

    @Before
    public void setup() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        db.collection("comments").get().addOnSuccessListener(querySnapshot -> {
            querySnapshot.forEach(doc -> doc.getReference().delete());
            latch.countDown();
        }).addOnFailureListener(e -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testAddCommentViaUI() throws InterruptedException {
        // Create and pass a dummy mood
        String moodDocId = "testMoodAdd_" + System.currentTimeMillis();
        Mood testMood = new Mood();
        testMood.setUsername("friendTest");
        testMood.setMoodState(Mood.MoodState.HAPPINESS);
        testMood.setTrigger("Testing mood");
        testMood.setDateTime(new Date());
        testMood.setDocId(moodDocId);

        String testUsername = "instrumentedUser";
        MyApp myApp = (MyApp) ApplicationProvider.getApplicationContext();
        myApp.setUsername(testUsername);

        // Launch FriendMoodEventDetailsActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendMoodEventDetailsActivity.class);
        intent.putExtra("mood", testMood);
        intent.putExtra("username", testUsername);
        ActivityScenario.launch(intent);

        // Click on comment button to open ViewCommentsActivity
        onView(withId(R.id.button_ActivityFriendMoodEventDetails_commentButton)).perform(click());

        Thread.sleep(1000); // wait for transition

        // Open Add Comment dialog
        onView(withId(R.id.imagebutton_ActivityViewComments_addcommentsbutton)).perform(click());

        Thread.sleep(500); // wait for dialog to appear

        // Type a comment and confirm
        String sampleComment = "Hello from test at " + System.currentTimeMillis();
        onView(withId(R.id.edittext_FragmentAddCommentsDialogFrag_editCommentText))
                .perform(replaceText(sampleComment));

        onView(withText("Post")).perform(click());

        Thread.sleep(1500); // allow Firebase save and UI update

        // Check the new comment is visible
        onView(allOf(withText(sampleComment), isDisplayed()))
                .check(matches(isDisplayed()));
    }
}
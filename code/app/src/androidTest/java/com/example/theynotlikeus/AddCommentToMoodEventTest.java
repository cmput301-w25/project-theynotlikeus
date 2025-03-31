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
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AddCommentToMoodEventTest {

    private static final String TAG = "AddCommentTest";

    @Test
    public void testAddCommentToMoodEvent() throws Exception {
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

        // Wait for UI to update.
        Thread.sleep(5000);

        // Verify that the new comment appears in the UI.
        onView(withText(testComment)).check(matches(isDisplayed()));

        // Now verify that the comment was saved in Firestore's top-level "comments" collection.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("comments").whereEqualTo("commentText", testComment);

        boolean commentFound = false;
        long startTime = System.currentTimeMillis();
        // Increase total timeout to 60 seconds for CI delays.
        long timeout = 60000;

        // Poll for the comment to appear in Firestore.
        while ((System.currentTimeMillis() - startTime) < timeout && !commentFound) {
            try {
                QuerySnapshot querySnapshot = Tasks.await(query.get(), 10, TimeUnit.SECONDS);
                List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                if (!docs.isEmpty()) {
                    commentFound = true;
                    Log.d(TAG, "Comment found in Firestore: " + docs.get(0).getData());
                } else {
                    Log.d(TAG, "Comment not found yet, retrying...");
                }
            } catch (Exception e) {
                Log.d(TAG, "Firestore query failed: " + e.getMessage());
            }
            if (!commentFound) {
                Thread.sleep(2000);
            }
        }
        assertTrue("The comment was not saved in Firestore", commentFound);
    }
}

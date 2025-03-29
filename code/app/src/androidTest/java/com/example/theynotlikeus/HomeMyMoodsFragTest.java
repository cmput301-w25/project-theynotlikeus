package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.content.res.Resources;
import android.os.SystemClock;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UI test for HomeMyMoodsFrag.
 *
 * This test adds 10 mood events with titles "Mood 1" through "Mood 10" to Firestore
 * (with increasing dateTime values so that "Mood 10" is the most recent) and then verifies that
 * the RecyclerView in HomeMyMoodsFrag displays them in descending order by date (i.e. the most recent mood
 * appears first). In our test data, the mood event "mood_10" has moodState "HAPPINESS" and "mood_9" has "BOREDOM".
 */
@RunWith(AndroidJUnit4.class)
public class HomeMyMoodsFragTest {

    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        // "10.0.2.2" allows the Android emulator to access localhost.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Helper method to add 10 mood events with different mood states to the local Firestore database.
     * The document IDs are set as "mood_1", "mood_2", ..., "mood_10".
     */
    private void addMoodsToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Array of mood states for testing.
        String[] moodStates = {
                "ANGER", "CONFUSION", "DISGUST", "FEAR", "HAPPINESS",
                "SADNESS", "SHAME", "SURPRISE", "BOREDOM", "HAPPINESS"
        };

        // Add 10 mood events with increasing dateTime values so that higher index = more recent.
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mood = new HashMap<>();
            // Add a title (for clarity, though your UI might display moodState instead).
            mood.put("title", "Mood " + (i + 1));
            mood.put("description", "Description for Mood " + (i + 1));
            // Set dateTime so that mood_10 is the most recent.
            mood.put("dateTime", new Date(System.currentTimeMillis() + i * 1000));
            // Use "defaultUser" as the username.
            mood.put("username", "defaultUser");
            // Set the mood state.
            mood.put("moodState", moodStates[i]);
            // Mark the mood as approved (pendingReview false).
            mood.put("pendingReview", false);

            // Write the mood data to the "moods" collection with document id "mood_(i+1)"
            db.collection("moods").document("mood_" + (i + 1)).set(mood);
        }
    }

    @Test
    public void testMoodsAreSortedByDate() throws InterruptedException {
        // Populate the local database with 10 mood events.
        addMoodsToDatabase();

        // Wait for data to be written.
        SystemClock.sleep(3000);

        // Launch MainActivity (which hosts HomeMyMoodsFrag).
        ActivityScenario.launch(MainActivity.class);

        // Wait for the fragment to load the moods.
        SystemClock.sleep(3000);

        // Verify that the RecyclerView is displayed.
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));

        // Verify that the first item displays "HAPPINESS" (from mood_10, the most recent)
        // and the second displays "BOREDOM" (from mood_9, the second most recent).
        onView(new RecyclerViewMatcher(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)
                .atPositionOnView(0, R.id.textview_fragmentmoodeventlayout_moodtitle))
                .check(matches(withText(containsString("HAPPINESS"))));

        onView(new RecyclerViewMatcher(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)
                .atPositionOnView(1, R.id.textview_fragmentmoodeventlayout_moodtitle))
                .check(matches(withText(containsString("BOREDOM"))));
    }

    /**
     * RecyclerViewMatcher helps find a child view at a given position within a RecyclerView.
     */
    public static class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
            return new BoundedMatcher<View, View>(View.class) {
                Resources resources = null;
                View childView;

                @Override
                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (resources != null) {
                        try {
                            idDescription = resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException e) {
                            idDescription = String.format("%d (resource name not found)", recyclerViewId);
                        }
                    }
                    description.appendText("with id: " + idDescription + " at position: " + position);
                }

                @Override
                protected boolean matchesSafely(View view) {
                    resources = view.getResources();
                    if (childView == null) {
                        androidx.recyclerview.widget.RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder == null) {
                                // Item not available yet.
                                return false;
                            }
                            childView = viewHolder.itemView;
                        } else {
                            return false;
                        }
                    }
                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }
                }
            };
        }
    }
}

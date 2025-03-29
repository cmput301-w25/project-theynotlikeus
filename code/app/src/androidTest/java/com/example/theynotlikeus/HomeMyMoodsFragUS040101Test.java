package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.os.SystemClock;
import android.view.View;
import android.content.res.Resources;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.theynotlikeus.view.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeMyMoodsFragUS040101Test {

    @BeforeClass
    public static void setup() {
        // Configure Firestore to use the local emulator.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        com.google.firebase.firestore.FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMoodsAreSortedByDate() {
        // Wait for the fragment and data to load (ideally, replace with an IdlingResource)
        SystemClock.sleep(5000);

        // Verify that the RecyclerView is displayed.
        onView(withId(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview))
                .check(matches(isDisplayed()));

        // Verify that the first item (position 0) contains the expected text (example: "Mood 10")
        onView(new RecyclerViewMatcher(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)
                .atPositionOnView(0, R.id.textView_moodText))
                .check(matches(withText(containsString("Mood 10"))));

        // Verify that the second item (position 1) contains the expected text (example: "Mood 9")
        onView(new RecyclerViewMatcher(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview)
                .atPositionOnView(1, R.id.textView_moodText))
                .check(matches(withText(containsString("Mood 9"))));
    }

    /**
     * Helper class to match a child view within a RecyclerView item at a given position.
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
            return new TypeSafeMatcher<View>() {
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
                public boolean matchesSafely(View view) {
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

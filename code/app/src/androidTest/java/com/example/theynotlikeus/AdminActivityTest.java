package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.theynotlikeus.view.AdminActivity;
import com.example.theynotlikeus.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * This test file tests the UI functionality of the AdminActivityfrag and fouces on user story 02.03.01
 * Verifies that the text about the photo storage limit is correctly displayed.
 * Verifies that toggling the switch updates SharedPreferences accordingly.
 */
@RunWith(AndroidJUnit4.class)
public class AdminActivityTest {

    @Rule
    public ActivityScenarioRule<AdminActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AdminActivity.class);

    /**
     * Test that verifies the TextView displays the string indicating that each photographic image
     * must be under 65,536 bytes (65 KB).
     */
    @Test
    public void testPicLimitTextDisplayed() {
        onView(withId(R.id.textView))
                .check(matches(withText(R.string.storage_for_each_photographic_image_to_be_under_65536_bytes)));
    }

    /**
     * Test that verifies toggling the switch (which controls the image size limit)
     * updates the SharedPreferences value accordingly.
     */
    @Test
    public void testSwitchToggleUpdatesSharedPreferences() throws InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE);
        boolean initialValue = prefs.getBoolean("limit_on", true);

        onView(withId(R.id.switch1)).perform(click());
        //Wait briefly for the update to take effect.
        Thread.sleep(1000);

        //After toggling, the new value should be the opposite of the initial value.
        boolean toggledValue = prefs.getBoolean("limit_on", !initialValue);
        assertEquals("Switch toggle did not update SharedPreferences correctly", !initialValue, toggledValue);

        //revert to the original value.
        onView(withId(R.id.switch1)).perform(click());
        Thread.sleep(1000);
        boolean finalValue = prefs.getBoolean("limit_on", toggledValue);
        assertEquals("Switch toggle did not revert SharedPreferences value", initialValue, finalValue);
    }
}

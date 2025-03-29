package com.example.theynotlikeus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import com.example.theynotlikeus.model.Mood;

import org.junit.Test;

public class CommunityMapActivityUnitTest {

    /**
     * Helper method that calculates the distance between two Mood objects using their latitude/longitude.
     * Returns true if the distance is within the given threshold (in meters), false otherwise.
     */
    private boolean areMoodsWithinDistance(Mood mood1, Mood mood2, float thresholdMeters) {
        float[] results = new float[1];
        Location.distanceBetween(
                mood1.getLatitude(), mood1.getLongitude(),
                mood2.getLatitude(), mood2.getLongitude(),
                results
        );
        return results[0] <= thresholdMeters;
    }

    @Test
    public void testMoodsWithinDistance() {
        // Create a mood for the user (e.g., near Googleplex)
        Mood userMood = new Mood(Mood.MoodState.HAPPINESS);
        userMood.setLocation(37.4220, -122.0841);

        // Create a mood for a friend that is very close (e.g., only a few meters apart)
        Mood friendMood = new Mood(Mood.MoodState.HAPPINESS);
        friendMood.setLocation(37.4225, -122.0838);

        // Check that they are within 5km.
        assertTrue("Mood events are within 5 km", areMoodsWithinDistance(userMood, friendMood, 5000));
    }

    @Test
    public void testMoodsOutsideDistance() {
        // Create a mood for the user.
        Mood userMood = new Mood(Mood.MoodState.HAPPINESS);
        userMood.setLocation(37.4220, -122.0841);

        // Create a mood for a friend that is far away (e.g., Los Angeles, CA)
        Mood friendMood = new Mood(Mood.MoodState.HAPPINESS);
        friendMood.setLocation(34.0522, -118.2437);

        // Check that they are NOT within 5km.
        assertFalse("Mood events are not within 5 km", areMoodsWithinDistance(userMood, friendMood, 5000));
    }
}

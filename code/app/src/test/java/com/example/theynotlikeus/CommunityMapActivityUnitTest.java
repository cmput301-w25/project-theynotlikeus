package com.example.theynotlikeus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.theynotlikeus.model.Mood;

import org.junit.Test;

public class CommunityMapActivityUnitTest {

    /**
     * Helper method that calculates the distance (in meters) between two latitude/longitude pairs
     * using the Haversine formula.
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth's radius in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Returns true if the distance between two Mood objects (using their latitude and longitude)
     * is less than or equal to the given threshold (in meters), false otherwise.
     */
    private boolean areMoodsWithinDistance(Mood mood1, Mood mood2, float thresholdMeters) {
        double distance = haversine(
                mood1.getLatitude(), mood1.getLongitude(),
                mood2.getLatitude(), mood2.getLongitude()
        );
        return distance <= thresholdMeters;
    }

    @Test
    public void testMoodsWithinDistance() {
        // Create a mood for the user (e.g., near Googleplex).
        Mood userMood = new Mood(Mood.MoodState.HAPPINESS);
        userMood.setLocation(37.4220, -122.0841);

        // Create a mood for a friend that is very close (e.g., only a few meters apart)
        Mood friendMood = new Mood(Mood.MoodState.HAPPINESS);
        friendMood.setLocation(37.4225, -122.0838);

        // They should be within 5 km.
        assertTrue("Moods are within 5 km", areMoodsWithinDistance(userMood, friendMood, 5000));
    }

    @Test
    public void testMoodsOutsideDistance() {
        // Create a mood for the user.
        Mood userMood = new Mood(Mood.MoodState.HAPPINESS);
        userMood.setLocation(37.4220, -122.0841);

        // Create a mood for a friend that is far away (e.g., Los Angeles, CA).
        Mood friendMood = new Mood(Mood.MoodState.HAPPINESS);
        friendMood.setLocation(34.0522, -118.2437);

        // They should not be within 5 km.
        assertFalse("Moods are not within 5 km", areMoodsWithinDistance(userMood, friendMood, 5000));
    }
}

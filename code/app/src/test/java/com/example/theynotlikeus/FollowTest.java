package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;

import com.example.theynotlikeus.model.Follow;

import org.junit.Before;
import org.junit.Test;

/*
Unit test for the follow model.
 */
public class FollowTest {
    private Follow follow;

    @Before
    public void setUp() {
        /*
        Sets up a new follow to test.
         */
        follow = new Follow("testFollowed", "testFollower");
    }

    // Test getters
    @Test
    public void testFollowGetters() {
        assertEquals("testFollowed", follow.getFollowed());
        assertEquals("testFollower", follow.getFollower());
    }

    // Test setters
    @Test
    public void testFollowSetters() {
        follow.setFollowed("user1");
        follow.setFollower("user2");

        assertEquals("user1", follow.getFollowed());
        assertEquals("user2", follow.getFollower());
    }
}
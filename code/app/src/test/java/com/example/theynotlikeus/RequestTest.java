package com.example.theynotlikeus;
import static org.junit.Assert.assertEquals;

import com.example.theynotlikeus.model.Request;

import org.junit.Before;
import org.junit.Test;

/*
Unit test for the request model.
 */
public class RequestTest {
    private Request request;

    @Before
    public void setUp() {
        /*
        Sets up a new request to test.
         */
        request = new Request("testFollower", "testFollowee");
    }

    // Test getters
    @Test
    public void testRequestGetters() {
        assertEquals("testFollower", request.getFollower());
        assertEquals("testFollowee", request.getFollowee());
    }

    // Test setters
    @Test
    public void testRequestSetters() {
        request.setFollower("user1");
        request.setFollowee("user2");

        assertEquals("user1", request.getFollower());
        assertEquals("user2", request.getFollowee());
    }
}
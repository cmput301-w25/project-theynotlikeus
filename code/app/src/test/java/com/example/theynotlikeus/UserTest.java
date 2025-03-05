package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/*
Unit test class for the User class.
Will test the User class.
 */

public class UserTest {
    private User user;

    @Before
    public void setUp() {
        /*
        Sets up a new user to test.
         */
        user = new User("testUser", "password");
    }

    @Test
    public void testGetUSername() {
        /*
        Tests getting the username
         */
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testSetUsername() {
        /*
        Tests setting a new username.
         */
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
    }

    @Test
    public void testGetPassword() {
        /*
        Tests getting a password.
         */
        assertEquals("password", user.getPassword());
    }

    @Test
    public void testSetPassword() {
        /*
        Tests setting anew password.
         */
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }
}

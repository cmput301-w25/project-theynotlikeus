package com.example.theynotlikeus;

/**
 * Represents a user with a username and password.
 *
 * Purpose:
 * Represents a user with login credentials.
 * Used for authentication and storing user data in Firestore.
 * Provides constructors required for Firestore integration.
 * Contains getters and setters for the username and password.
 */
public class User {

    //Field to store the user's username.
    private String username;
    //Field to store the user's password.
    private String password;


    public User() {
        //Empty constructor to satisfy Firestore requirement.
    }

    /**
     * Constructor to create a User object with specified username and password.
     *
     * @param username the username of the user.
     * @param password the password for the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return the user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username for the user.
     *
     * @param username the new username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the user's password.
     *
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the user.
     *
     * @param password the new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
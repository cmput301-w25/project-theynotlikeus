package com.example.theynotlikeus.model;
/*
    User class with a username and password.
*   Used for authentication and storing user data in Firestore.
* */
/**
 * The User class represents a user with a username and password.
 * This class is used for authentication and storing user data in Firestore.
 * An empty constructor is provided for Firestore deserialization.
 */
public class User {

    //Field to store the user's username.
    private String username;
    //Field to store the user's password.
    private String password;


    public User(){}; //Empty constructor required for Firestore.

    /**
     * This will construct a new user with a username and password.
     * @param username
     * @param password
     */
    public User(String username, String password){
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
        //Retrieves the username of the user.
    }

    /**
     * Sets a new username for the user.
     *
     * @param username the new username to set.
     */
    public void setUsername(String username) {
        this.username = username;
        //Sets a new password for the user.
    }

    /**
     * Retrieves the user's password.
     *
     * @return the user's password.
     */
    public String getPassword() {
        return password;
        //Retrieves the user's password.
    }

    /**
     * Sets a new password for the user.
     *
     * @param password the new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
        //Sets a new password for the user.
    }


}

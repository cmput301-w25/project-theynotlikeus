package com.example.theynotlikeus;
/*
    User class with a username and password.
*   Used for authentication and storing user data in Firestore.
* */
public class User {
    private  String username;
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
     * Gets the username
     * @return the username
     */
    public  String getUsername(){
        return username;
        //Retrieves the username of the user.
    }

    /**
     * Sets a new username
     * @param username new username
     */
    public void setUsername(String username){
        this.username = username;
        //Sets a new password for the user.
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword(){
        return password;
        //Retrieves the user's password.
    }

    /**
     * Sets a new password
     * @param password new password
     */
    public void setPassword( String password){
        this.password = password;
        //Sets a new password for the user.
    }


}

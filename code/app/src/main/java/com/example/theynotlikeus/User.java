package com.example.theynotlikeus;
/*
    User class with a username and password.
*   Used for authentication and storing user data in Firestore.
* */
public class User {
    private  String username;
    private String password;


    public User(){}; //Empty constructor required for Firestore.
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public  String getUsername(){
        return username;
        //Retrieves the username of the user.
    }

    public void setUsername(String username){
        this.username = username;
        //Sets a new password for the user.
    }

    public String getPassword(){
        return password;
        //Retrieves the user's password.
    }

    public void setPassword( String password){
        this.password = password;
        //Sets a new password for the user.
    }


}

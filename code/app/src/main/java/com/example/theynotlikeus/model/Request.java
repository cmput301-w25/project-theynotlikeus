
package com.example.theynotlikeus.model;

public class Request {
    private String id;
    private String follower;
    private String followee;

    // Required empty constructor for Firestore
    public Request() {}

    // Getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFollower() {
        return follower;
    }
    public void setFollower(String follower) {
        this.follower = follower;
    }
    public String getFollowee() {
        return followee;
    }
    public void setFollowee(String followee) {
        this.followee = followee;
    }
}

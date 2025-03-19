package com.example.theynotlikeus.model;

/**
 * A follow class will keep the ID of the users and keep track of who is followed and who is a follower.
 * Used to search new users to follow.
 * An empty constructor is provided for Firestore deserialization.
 */
public class Follow {
    private String id;
    private String followed;
    private String follower;

    public Follow() {} // Empty constructor required for Firestore

    public Follow(String id, String followed, String follower) {
        this.id = id;
        this.followed = followed;
        this.follower = follower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }
}

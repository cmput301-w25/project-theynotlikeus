package com.example.theynotlikeus.model;

/**
 * A follow class will keep the ID of the users and keep track of who is followed and who is a follower.
 * Used to search new users to follow.
 * An empty constructor is provided for Firestore deserialization.
 */
public class Follow {
    private String followed;
    private String follower;

    public Follow() {} // Empty constructor required for Firestore

    /**
     * Construct the id of a follow along with whether the user is followed or a follower
     * @param followed
     * @param follower
     */
    public Follow(String followed, String follower) {
        this.followed = followed;
        this.follower = follower;
    }

    /**
     * Returns a string of who is followed
     * @return followed
     */
    public String getFollowed() {
        return followed;
    }

    /**
     * Sets the followed
     * @param followed
     */
    public void setFollowed(String followed) {
        this.followed = followed;
    }

    /**
     * Gets the follower
     * @return follower
     */
    public String getFollower() {
        return follower;
    }

    /**
     * Sets the follower.
     * @param follower
     */
    public void setFollower(String follower) {
        this.follower = follower;
    }
}

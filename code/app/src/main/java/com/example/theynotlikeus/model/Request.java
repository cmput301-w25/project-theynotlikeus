
package com.example.theynotlikeus.model;

/**
 * Represents a request between two users.
 *
 * Purpose:
 * A class that represents a follow request between two users.
 * Class includes setters and getters fir all attributes.
 * Provides a no-argument constructor for Firestore deserialization.
 */
public class Request {
    private String id;
    private String follower;
    private String followee;

    // Required empty constructor for Firestore
    public Request() {}

    /**
     * This will construct a new request between two users.
     * @param follower
     * @param followee
     */
    public Request(String follower, String followee){
        this.follower = follower;
        this.followee = followee;
    }

    // Getters and setters

    /**
     * Returns the docId of the request document from the request collection.
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the docId
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the follower, the person creating the follow request.
     * @return
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

    /**
     * Gets the followee, the person getting the follow request from the follower.
     * @return
     */
    public String getFollowee() {
        return followee;
    }

    /**
     * Sets the followee.
     * @param followee
     */
    public void setFollowee(String followee) {
        this.followee = followee;
    }
}
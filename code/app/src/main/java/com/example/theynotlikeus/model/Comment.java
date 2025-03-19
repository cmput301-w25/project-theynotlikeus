package com.example.theynotlikeus.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private String associatedMoodID;
    private String commentAuthor;
    private String commentText;     // So far, no comment limit. Consult manager.
    private Date commentDateTime;


    /**
     * Constructor
     */
    public Comment() {
        // Empty instance to satisfy Firestore requirement.
    }

    // Getters and setters
    public String getAssociatedMoodID() {
        return associatedMoodID;
    }

    public void setAssociatedMoodID(String associatedMoodID) {
        this.associatedMoodID = associatedMoodID;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getCommentDateTime() {
        return commentDateTime;
    }

    public void setCommentDateTime() {
        this.commentDateTime = new Date();
    }






}

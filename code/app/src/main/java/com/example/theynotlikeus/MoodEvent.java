package com.example.theynotlikeus;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class MoodEvent implements Serializable {
    // Identification
    private String userID;      // This is the document ID of the user who created the mood event
    private String moodEventID;     // This is the document ID of the mood event document

    // User story attributes
    private String moodEventDate;
    private String moodEventTime;
    private String moodEventEmotionalState;
    private String moodEventOptionalTrigger;
    private String moodEventOptionalSocialSituation;

    // Peripheral attributes
    private String moodEventLatitude;
    private String moodEventLongitude;
    private Integer moodEventPhotoSize;

    // Constructor
    public MoodEvent(String userID, String moodEventID, String moodEventDate, String moodEventTime, String moodEventEmotionalState,
                     @Nullable String moodEventOptionalTrigger, @Nullable String moodEventOptionalSocialSituation,
                     @Nullable String moodEventLatitude, @Nullable String moodEventLongitude, @Nullable Integer moodEventPhotoSize) {
        this.userID = userID;
        this.moodEventID = moodEventID;
        this.moodEventDate = moodEventDate;
        this.moodEventTime = moodEventTime;
        this.moodEventEmotionalState = moodEventEmotionalState;
        this.moodEventOptionalTrigger = moodEventOptionalTrigger;
        this.moodEventOptionalSocialSituation = moodEventOptionalSocialSituation;
        this.moodEventLatitude = moodEventLatitude;
        this.moodEventLongitude = moodEventLongitude;
        this.moodEventPhotoSize = moodEventPhotoSize;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMoodEventID() {
        return moodEventID;
    }

    public void setMoodEventID(String moodEventID) {
        this.moodEventID = moodEventID;
    }

    public String getMoodEventDate() {
        return moodEventDate;
    }

    public void setMoodEventDate(String moodEventDate) {
        this.moodEventDate = moodEventDate;
    }

    public String getMoodEventTime() {
        return moodEventTime;
    }

    public void setMoodEventTime(String moodEventTime) {
        this.moodEventTime = moodEventTime;
    }

    public String getMoodEventEmotionalState() {
        return moodEventEmotionalState;
    }

    public void setMoodEventEmotionalState(String moodEventEmotionalState) {
        this.moodEventEmotionalState = moodEventEmotionalState;
    }

    public String getMoodEventOptionalTrigger() {
        return moodEventOptionalTrigger;
    }

    public void setMoodEventOptionalTrigger(String moodEventOptionalTrigger) {
        this.moodEventOptionalTrigger = moodEventOptionalTrigger;
    }

    public String getMoodEventOptionalSocialSituation() {
        return moodEventOptionalSocialSituation;
    }

    public void setMoodEventOptionalSocialSituation(String moodEventOptionalSocialSituation) {
        this.moodEventOptionalSocialSituation = moodEventOptionalSocialSituation;
    }

    public String getMoodEventLatitude() {
        return moodEventLatitude;
    }

    public void setMoodEventLatitude(String moodEventLatitude) {
        this.moodEventLatitude = moodEventLatitude;
    }

    public String getMoodEventLongitude() {
        return moodEventLongitude;
    }

    public void setMoodEventLongitude(String moodEventLongitude) {
        this.moodEventLongitude = moodEventLongitude;
    }

    public int getMoodEventPhotoSize() {
        return moodEventPhotoSize;
    }

    public void setMoodEventPhotoSize(Integer moodEventPhotoSize) {
        this.moodEventPhotoSize = moodEventPhotoSize;
    }

}

package com.example.theynotlikeus.model;

import android.content.SharedPreferences;
import com.example.theynotlikeus.view.AdminActivity;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a mood event for a user.
 *
 * Purpose:
 * - Encapsulates details of a user's mood entry, including mood state, trigger, social situation,
 *   photo, location, and username.
 * - Provides a no-argument constructor for Firestore deserialization.
 * - Contains getters and setters for each property.
 * - Includes additional metadata such as a document ID for Firestore reference.
 */
public class Mood implements Serializable {



    /**
     * Enumeration for the mood state.
     */
    public enum MoodState {
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM
    }

    /**
     * Enumeration for the social situation during the mood event.
     */
    public enum SocialSituation {
        ALONE, ONE_TO_OTHER, TWO_TO_SEVERAL, TO_CROWD
    }

    // Private fields for storing mood details.
    private Date dateTime;
    private MoodState moodState;
    private String trigger;
    private SocialSituation socialSituation;

    //private byte[] photo;
    private String photoUrl;
    private byte[] photo;
    private int photoSize = 65536;
    private Double latitude;
    private Double longitude;
    private String username;

    // Document ID for Firestore reference.
    private String docId;
    private boolean isPublic = false;
    private SharedPreferences prefs;
    private boolean isLimitEnabled;

    /**
     * No-argument constructor required by Firestore for automatic deserialization.
     */
    public Mood() {
        // Empty instance to satisfy Firestore requirement.
    }

    /**
     * Retrieves the document ID associated with this mood entry.
     *
     * @return the document ID.
     */
    public String getDocId() {
        return docId;
    }

    /**
     * Sets the document ID for this mood entry.
     *
     * @param docId the document ID to set.
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * Constructor for creating a mood entry with the current date.
     *
     * @param moodState the mood state for the entry.
     */
    public Mood(MoodState moodState) {
        this.dateTime = new Date();
        this.moodState = moodState;
    }

    /**
     * Constructor for creating a mood entry with a specified date.
     *
     * @param dateTime  the date and time of the mood event.
     * @param moodState the mood state for the entry.
     */
    public Mood(Date dateTime, MoodState moodState) {
        this.dateTime = dateTime;
        this.moodState = moodState;
    }

    /**
     * Gets the date and time of the mood event.
     *
     * @return the date and time.
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the mood event.
     *
     * @param dateTime the date and time to set.
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the mood state of the event.
     *
     * @return the mood state.
     */
    public MoodState getMoodState() {
        return moodState;
    }

    /**
     * Sets the mood state for the event.
     *
     * @param moodState the mood state to set.
     */
    public void setMoodState(MoodState moodState) {
        this.moodState = moodState;
    }

    /**
     * Gets the trigger for the mood event.
     *
     * @return the trigger.
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * Sets the trigger for the mood event.
     *
     * @param trigger the trigger to set.
     */
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    /**
     * Gets the social situation of the mood event.
     *
     * @return the social situation.
     */
    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    /**
     * Sets the social situation for the mood event.
     *
     * @param socialSituation the social situation to set.
     */
    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    /**
     * Gets the maximum allowed photo size.
     *
     * @return the photo size in bytes.
     */
    public int getPhotoSize() {
        return photoSize;
    }

    /**
     * Sets the maximum allowed photo size.
     *
     * @param photoSize the photo size in bytes.
     */
    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    /**
     * Gets the photo url.
     * @return photoUrl as a string
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Sets the photo Url
     * @param photoUrl
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Gets the latitude coordinate of the mood event.
     *
     * @return the latitude.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude coordinate of the mood event.
     *
     * @return the longitude.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the location (latitude and longitude) of the mood event.
     *
     * @param latitude  the latitude to set.
     * @param longitude the longitude to set.
     */
    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the username associated with the mood event.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the mood event.
     *
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the visibility status of the mood event.
     *
     * @return true if public, false if private.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Sets the visibility status of the mood event.
     *
     * @param isPublic true to make public, false to make private.
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Returns a string representation of the Mood object.
     *
     * @return a formatted string containing mood details.
     */
    @Override
    public String toString() {
        return "Mood{" +
                "dateTime=" + dateTime +
                ", moodState=" + moodState +
                ", trigger='" + trigger + '\'' +
                ", socialSituation=" + socialSituation +
                ", photo=" + (photoUrl != null ? "present" : "none") +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                '}';
    }
}
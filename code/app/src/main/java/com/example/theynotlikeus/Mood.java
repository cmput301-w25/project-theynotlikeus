package com.example.theynotlikeus;

import android.content.SharedPreferences;

import java.util.Date;

/**
 * Represents a mood event for a user.
 *
 * Purpose:
 * - Encapsulates details of a user's mood entry, including mood state, trigger, social situation,
 *   reason, photo, location, and username.
 * - Provides a no-argument constructor for Firestore deserialization.
 * - Contains getters and setters for each property.
 * - Includes additional metadata such as a document ID for Firestore reference.
 */
public class Mood {

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
    private String reason;
    private byte[] photo;
    private int photoSize = 65536;
    private Double latitude;
    private Double longitude;
    private String username;

    // Document ID for Firestore reference.
    private String docId;
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
     * Gets the reason for the mood event.
     *
     * @return the reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the mood event.
     * Ensures that the reason does not exceed 20 characters or 3 words.
     *
     * @param reason the reason to set.
     * @throws IllegalArgumentException if the reason exceeds allowed length or word count.
     */
    public void setReason(String reason) {

        if (reason != null && (reason.length() > 20 || reason.split("\\s+").length > 3)) {
            throw new IllegalArgumentException("Reason must be at most 20 characters or 3 words.");
        }
        this.reason = reason;
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
     * Gets the photo associated with the mood event.
     *
     * @return the photo as a byte array.
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Sets the photo for the mood event.
     * Ensures that the photo size is within the allowed limit.
     *
     * @param photo the photo as a byte array.
     * @throws IllegalArgumentException if the photo exceeds the allowed size.
     */
    public void setPhoto(byte[] photo) {
        boolean isLimitEnabled = true; // Default to true if prefs is null
        if (prefs != null) {
            isLimitEnabled = AdminActivity.isLimitEnabled(prefs);
        }
        if (isLimitEnabled  && photo != null && photo.length > photoSize) {
            throw new IllegalArgumentException("Photo size must be under 65536 bytes.");
        }
        this.photo = photo;
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
                ", reason='" + reason + '\'' +
                ", photo=" + (photo != null ? "present" : "none") +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                '}';
    }

}
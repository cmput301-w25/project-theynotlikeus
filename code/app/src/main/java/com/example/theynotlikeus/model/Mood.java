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
 *   reason, photo, location, and username.
 * - Provides a no-argument constructor for Firestore deserialization.
 * - Contains getters and setters for each property.
 * - Includes additional metadata such as a document ID for Firestore reference.
 */
public class Mood implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // New field to mark if a mood is pending admin review.
    private boolean pendingReview = false;

    private SharedPreferences prefs;
    private boolean isLimitEnabled;

    /**
     * No-argument constructor required by Firestore for automatic deserialization.
     */
    public Mood() {
        // Empty instance to satisfy Firestore requirement.
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

    // Getters and Setters.

    public String getDocId() {
        return docId;
    }
    public void setDocId(String docId) {
        this.docId = docId;
    }
    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public MoodState getMoodState() {
        return moodState;
    }
    public void setMoodState(MoodState moodState) {
        this.moodState = moodState;
    }
    public String getTrigger() {
        return trigger;
    }
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    public SocialSituation getSocialSituation() {
        return socialSituation;
    }
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
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isPendingReview() {
        return pendingReview;
    }
    public void setPendingReview(boolean pendingReview) {
        this.pendingReview = pendingReview;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "dateTime=" + dateTime +
                ", moodState=" + moodState +
                ", trigger='" + trigger + '\'' +
                ", socialSituation=" + socialSituation +
                ", reason='" + reason + '\'' +
                ", photo=" + (photoUrl != null ? "present" : "none") +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                ", isPublic=" + isPublic +
                ", pendingReview=" + pendingReview +
                '}';
    }
}

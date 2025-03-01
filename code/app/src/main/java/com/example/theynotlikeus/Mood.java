package com.example.theynotlikeus;

import java.util.Date;
/*
*   Represents a user's mood entry.
*   Contains mood state, social situation, reason, location, and optional photo.
*
* */
public class Mood {

    public enum MoodState {
        // Enum representing different mood states.
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM
    }

    public enum SocialSituation {
        // Enum representing social situations in which the mood was recorded.
        ALONE, ONE_TO_OTHER, TWO_TO_SEVERAL, TO_CROWD
    }

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

    public Mood() {
        // No-argument constructor required for Firestore.
    }
    //Constructor for creating a mood entry with the current date.
    public Mood(MoodState moodState) {
        this.dateTime = new Date();
        this.moodState = moodState;
    }

    //Constructor for creating a mood entry with a specified date.
    public Mood(Date dateTime, MoodState moodState) {
        this.dateTime = dateTime;
        this.moodState = moodState;
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

    public String getReason() {
        return reason;
    }
    //Sets the reason for the mood event(ensures the reason does not exceed 20 characters or 3 words).
    public void setReason(String reason) {
        if (reason != null && (reason.length() > 20 || reason.split("\\s+").length > 3)) {
            throw new IllegalArgumentException("Reason must be at most 20 characters or 3 words.");
        }
        this.reason = reason;
    }

    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public byte[] getPhoto() {
        return photo;
    }
    //Sets the photo for the mood entry(ensures the photo size is within the allowed limit).
    public void setPhoto(byte[] photo) {
        if (photo != null && photo.length > photoSize) {
            throw new IllegalArgumentException("Photo size must be under 65536 bytes.");
        }
        this.photo = photo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    //Sets the location (latitude and longitude) of the mood event.
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

package com.example.theynotlikeus;

import java.util.Date;

public class Mood {

    public enum MoodState {
        // Enumeration for the mood state, all required moods available
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM
    }

    public enum SocialSituation {
        // Enumeration for social situation of the user on given event.
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

    public Mood(MoodState moodState) {
        this.dateTime = new Date();
        this.moodState = moodState;
    }

    public Mood(Date dateTime, MoodState moodState) {
        this.dateTime = dateTime;
        this.moodState = moodState;
    }

    public Date getDateTime() {
        return dateTime;
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
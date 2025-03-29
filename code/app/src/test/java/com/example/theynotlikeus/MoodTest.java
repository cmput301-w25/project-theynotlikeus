package com.example.theynotlikeus;

import static org.junit.Assert.*;

import com.example.theynotlikeus.model.Mood;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class MoodTest {
    private Mood mood;

    @Before
    public void setUp() {
        //creating a mood object for the purpose of testing
        mood=new Mood(Mood.MoodState.HAPPINESS);
    }

    @Test
    public void testConstructorWithMoodState() {
        assertNotNull(mood.getDateTime());  //Ensures date is initialized
        assertEquals(Mood.MoodState.HAPPINESS, mood.getMoodState());
    }

    @Test
    public void testConstructorWithDateAndMoodState() {
        //Verifying the constructor works
        Date date=new Date();
        Mood moodWithDate=new Mood(date, Mood.MoodState.SADNESS);
        assertEquals(date, moodWithDate.getDateTime());
        assertEquals(Mood.MoodState.SADNESS, moodWithDate.getMoodState());
    }

    @Test
    public void testSetAndGetDateTime() {
        Date newDate=new Date();
        mood.setDateTime(newDate);
        assertEquals(newDate, mood.getDateTime());
    }

    @Test
    public void testSetAndGetMoodState() {
        mood.setMoodState(Mood.MoodState.ANGER);
        assertEquals(Mood.MoodState.ANGER, mood.getMoodState());
    }

    @Test
    public void testSetAndGetTrigger() {
        mood.setTrigger("Exam stress");
        assertEquals("Exam stress", mood.getTrigger());
    }

    @Test
    public void testSetAndGetSocialSituation() {
        mood.setSocialSituation(Mood.SocialSituation.ONE_TO_OTHER);
        assertEquals(Mood.SocialSituation.ONE_TO_OTHER, mood.getSocialSituation());
    }

    @Test
    public void testSetAndGetPhotoUrl() {
        mood.setPhotoUrl("http://example.com/photo.jpg");
        assertEquals("http://example.com/photo.jpg", mood.getPhotoUrl());
    }

    @Test
    public void testSetAndGetPhotoSize() {
        mood.setPhotoSize(50000);
        assertEquals(50000, mood.getPhotoSize());
    }

    @Test
    public void testSetAndGetLocation() {
        mood.setLocation(45.0, 90.0);
        assertEquals(45.0, mood.getLatitude(), 0.001);
        assertEquals(90.0, mood.getLongitude(), 0.001);
    }

    @Test
    public void testSetAndGetUsername() {
        mood.setUsername("testUser");
        assertEquals("testUser", mood.getUsername());
    }

    @Test
    public void testToString() {
        mood.setTrigger("Workload");
        mood.setSocialSituation(Mood.SocialSituation.ALONE);
        mood.setLocation(12.34, 56.78);
        mood.setPhotoUrl("http://example.com/photo.jpg");

        String moodString=mood.toString();
        assertTrue(moodString.contains("HAPPINESS"));
        assertTrue(moodString.contains("Workload"));
        assertTrue(moodString.contains("ALONE"));
        assertTrue(moodString.contains("(12.34, 56.78)"));
        assertTrue(moodString.contains("present"));
    }
}
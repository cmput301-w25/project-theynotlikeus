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
    public void testSetAndGetReasonValid() {
        mood.setReason("Too tired");
        assertEquals("Too tired", mood.getReason());
    }

    @Test
    public void testSetReasonInvalidTooLong() {
        assertThrows(IllegalArgumentException.class, () -> mood.setReason("This reason is way too long"));
    }

    @Test
    public void testSetReasonInvalidTooManyWords() {
        assertThrows(IllegalArgumentException.class, () -> mood.setReason("Too many words here"));
    }

    @Test
    public void testSetAndGetPhotoValid() {
        byte[] validPhoto=new byte[5000];  //Within the size limit
        mood.setPhoto(validPhoto);
        assertArrayEquals(validPhoto, mood.getPhoto());
    }

    @Test
    public void testSetPhotoInvalidTooLarge() {
        byte[] oversizedPhoto=new byte[70000];  //Exceeds 65536 bytes
        assertThrows(IllegalArgumentException.class, () -> mood.setPhoto(oversizedPhoto));
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
        mood.setReason("Too stressed");
        mood.setLocation(12.34, 56.78);
        mood.setPhoto(new byte[5000]);

        String moodString=mood.toString();
        assertTrue(moodString.contains("HAPPINESS"));
        assertTrue(moodString.contains("Workload"));
        assertTrue(moodString.contains("Too stressed"));
        assertTrue(moodString.contains("(12.34, 56.78)"));
        assertTrue(moodString.contains("present"));
    }
}

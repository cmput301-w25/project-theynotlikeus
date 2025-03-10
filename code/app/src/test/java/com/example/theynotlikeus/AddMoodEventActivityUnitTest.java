package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class AddMoodEventActivityUnitTest {

    @Test
    public void testValidateTriggerTooLong() {
        String trigger = "The Los Angeles Lakers are winning the NBA Championship in 2025.";
        int limit = 20;
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            AddMoodEventActivity.validateTrigger(trigger, limit);
        });
        assertEquals("Trigger has too many characters!", exception.getMessage());
    }

    @Test
    public void testValidateTriggerValid() {
        String trigger = "Lakers goat";
        int limit = 20;
        // Should not throw an exception
        AddMoodEventActivity.validateTrigger(trigger, limit);
    }

    @Test
    public void testMoodValid() {
        Mood.MoodState state = AddMoodEventActivity.parseMood("Happiness");
        assertEquals(Mood.MoodState.HAPPINESS, state);
    }

    @Test
    public void testMoodInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AddMoodEventActivity.parseMood("invalidMood");
        });
        assertEquals("Invalid mood selection.", exception.getMessage());
    }
}

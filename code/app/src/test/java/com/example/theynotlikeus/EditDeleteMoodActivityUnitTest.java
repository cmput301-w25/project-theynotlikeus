package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class EditDeleteMoodActivityUnitTest {

    private static final int TRIGGER_LENGTH_LIMIT = 20;

    private void checkTriggerLength(String trigger) {
        //This function checks whether the trigger value exceeds the character limit
        if (trigger.length() > TRIGGER_LENGTH_LIMIT) {
            throw new ArithmeticException("Trigger has too many characters!");
        }
    }

    private Mood.MoodState parseMoodState(String selectedMood) {
        //This function converts the input to uppercase and match an enum constant
        try {
            return Mood.MoodState.valueOf(selectedMood.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid mood selection.");
        }
    }


    @Test
    public void testTriggerShort_ok() {
        //This tests the situation when the trigger value is less than 20 characters
        String shortTrigger = "Short Trigger";
        checkTriggerLength(shortTrigger);
    }

    @Test
    public void testTriggerTooLong_throwsException() {
        //This test checks whether an exception is thrown when the trigger value has more than 20 characters
        String longTrigger = "This trigger definitely exceeds 20 characters.";
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> {
            checkTriggerLength(longTrigger);
        });
        assertEquals("Trigger has too many characters!", ex.getMessage());
    }


    @Test
    public void testParseMoodState_valid() {
        //This tests the situation when a user successfully selected one of the mood states from the list
        Mood.MoodState state = parseMoodState("Happiness");
        assertEquals(Mood.MoodState.HAPPINESS, state);
    }


    @Test
    public void testParseMoodState_invalid() {
        //This tests the situation when a user unsuccessfully selected one of the mood states from the list
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            parseMoodState("NotAMood");
        });
        assertEquals("Invalid mood selection.", ex.getMessage());
    }
}

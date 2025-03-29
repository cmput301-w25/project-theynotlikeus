package com.example.theynotlikeus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.theynotlikeus.model.Comment;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/*
Unit test for the comment model.
 */
public class CommentTest {
    private Comment comment;
    private Date testDate;

    @Before
    public void setUp() {
        /*
        Sets up a new Comment instance for testing.
        */
        testDate = new Date();
        comment = new Comment("Test comment", testDate);
    }

    // Test getters
    @Test
    public void testCommentGetters() {
        assertEquals("Test comment", comment.getCommentText());
        assertNull(comment.getAssociatedMoodID());
        assertNull(comment.getCommentAuthor());
        assertNull(comment.getCommentDateTime());
    }

    // Test setters
    @Test
    public void testCommentSetters() {
        comment.setAssociatedMoodID("mood123");
        comment.setCommentAuthor("user456");
        comment.setCommentText("Updated comment");

        assertEquals("mood123", comment.getAssociatedMoodID());
        assertEquals("user456", comment.getCommentAuthor());
        assertEquals("Updated comment", comment.getCommentText());
    }

    @Test
    public void testSetCommentDateTime() {
        comment.setCommentDateTime();
        assertEquals(new Date().getTime(), comment.getCommentDateTime().getTime(), 1000); // Allow slight difference
    }
}
package com.example.theynotlikeus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class UserRecyclerViewAdapterTest {

    @Mock
    private Context mockContext;
    private UserRecyclerViewAdapter adapter;
    private List<Mood> mockMoodList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMoodList = Arrays.asList(
                new Mood("testUser", Mood.MoodState.HAPPINESS, "Party"),
                new Mood("testUser2", Mood.MoodState.SADNESS, "Alone")
        );
        adapter = new UserRecyclerViewAdapter(mockContext, mockMoodList);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testOnCreateViewHolder() {
        ViewGroup parent = mock(ViewGroup.class);
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
    }
}

package com.example.theynotlikeus.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.theynotlikeus.R;

/**
 * Fragment that provides filtering options for mood events.
 *
 * Purpose:
 * Allows users to filter mood events based on different criteria.
 * Provides a dedicated UI layout for filter options.
 */
public class HomeMyMoodsFilterFrag extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_my_moods_filter, container, false);// Inflate the layout for this fragment
    }
}
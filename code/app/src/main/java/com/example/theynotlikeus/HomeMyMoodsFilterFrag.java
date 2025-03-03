package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment that provides filtering options for mood events.
 *
 * Purpose:
 * - Allows users to filter mood events based on different criteria.
 * - Provides a dedicated UI layout for filter options.
 */
public class HomeMyMoodsFilterFrag extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment from fragment_home_my_moods_filter.xml.
        return inflater.inflate(R.layout.fragment_home_my_moods_filter, container, false);
    }
}

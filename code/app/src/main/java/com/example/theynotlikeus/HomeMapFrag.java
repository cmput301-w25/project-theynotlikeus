package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment that displays a map for mood-related locations.
 *
 * Purpose:
 * Visualizes mood events on a map.
 * Can be used to display geographical locations associated with mood events.
 */
public class HomeMapFrag extends Fragment {

    // Parameter argument keys for potential customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters for fragment initialization. These can be used to pass data into the fragment.
    private String mParam1;
    private String mParam2;

    public HomeMapFrag() {
        // Required empty public constructor for proper
        // fragment instantiation and for Firebase's automatic deserialization.
    }

    //Factory method to create a new instance of HomeMapFrag using the provided parameters.
    public static HomeMapFrag newInstance(String param1, String param2) {
        HomeMapFrag fragment = new HomeMapFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieve parameters passed via the fragment's arguments.
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_map, container, false); //Inflate and return the layout for this fragment from fragment_home_map.xml.
    }
}
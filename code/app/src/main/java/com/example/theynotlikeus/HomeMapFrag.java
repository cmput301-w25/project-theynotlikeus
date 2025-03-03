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
 * - Visualizes mood events on a map.
 * - Can be used to display geographical locations associated with mood events.
 */
public class HomeMapFrag extends Fragment {

    // Parameter argument keys for potential customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters for fragment initialization. These can be used to pass data into the fragment.
    private String mParam1;
    private String mParam2;

    /**
     * Required empty public constructor.
     * This is necessary for proper fragment instantiation and for Firebase's automatic deserialization.
     */
    public HomeMapFrag() {
        // Required empty public constructor for Firebase
    }

    /**
     * Factory method to create a new instance of HomeMapFrag using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeMapFrag.
     */
    public static HomeMapFrag newInstance(String param1, String param2) {
        HomeMapFrag fragment = new HomeMapFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created.
     * Retrieves initialization parameters if they exist.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve parameters passed via the fragment's arguments.
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater LayoutInflater used to inflate the fragment's layout.
     * @param container Parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and return the layout for this fragment from fragment_home_map.xml.
        return inflater.inflate(R.layout.fragment_home_map, container, false);
    }
}

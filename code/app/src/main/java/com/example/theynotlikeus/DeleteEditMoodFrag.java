package com.example.theynotlikeus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * This fragment allows the user to edit and delete a mood event by providing a user interface for modifying stored mood data
 * including interactive buttons like save and delete.
 */

public class DeleteEditMoodFrag extends Fragment {

    //Parameter keys for future customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Variables to hold the parameters passed into the fragment.
    private String mParam1;
    private String mParam2;


    public DeleteEditMoodFrag() {
        //Required empty public constructor
    }


     //Factory method to create a new instance of DeleteEditMoodFrag using the provided parameters.
    public static DeleteEditMoodFrag newInstance(String param1, String param2) {
        DeleteEditMoodFrag fragment = new DeleteEditMoodFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve parameters from the arguments bundle if available.
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_delete_mood, container, false);
    }
}

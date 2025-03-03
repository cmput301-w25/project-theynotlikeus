package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment provides a user interface for the community area where users can interact with each other
 * and displays content related to community interactions and discussions for the community section of the app.
 */
public class CommunityFrag extends Fragment {

    //Parameter keys for customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Variables to hold initialization parameters.
    private String mParam1;
    private String mParam2;


    public CommunityFrag() {
        //Required empty public constructor.
    }

     //Factory method to create a new instance of CommunityFrag using the provided parameters.
    public static CommunityFrag newInstance(String param1, String param2) {
        CommunityFrag fragment = new CommunityFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieving initialization parameters from the arguments Bundle.
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false); //Inflate and return the layout for this fragment from fragment_community.xml.
    }
}

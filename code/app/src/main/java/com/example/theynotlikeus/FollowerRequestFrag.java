package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment displays and manages incoming and outgoing friend requests and provides a UI layout where users can review friend requests
 * for handling friend requests.
 *
 */
public class FollowerRequestFrag extends Fragment {

    //Parameter keys for potential customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Variables to hold the initialization parameters.
    private String mParam1;
    private String mParam2;


    public FollowerRequestFrag() {
        //Required empty public constructor
    }


     //Factory method to create a new instance of FriendRequestFrag using the provided parameters.
    public static FollowerRequestFrag newInstance(String param1, String param2) {
        FollowerRequestFrag fragment = new FollowerRequestFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieve initialization parameters
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follower_request, container, false); //Inflate and return the layout for this fragment.
    }
}

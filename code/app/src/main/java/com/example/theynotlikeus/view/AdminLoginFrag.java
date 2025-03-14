package com.example.theynotlikeus.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.theynotlikeus.R;
import com.google.android.material.appbar.MaterialToolbar;

/**
 *This fragment for admin login that allows navigation back to the user selection screen and
 *provides a UI for admin users to sign in and access the admin panel.
 *Note:Admins can only be registered via Firebase.
 */
public class AdminLoginFrag extends Fragment {


    //Parameter keys for fragment initialization (currently placeholders).
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Variables to hold initialization parameters.
    private String mParam1;
    private String mParam2;

    public AdminLoginFrag() {
        // Required empty public constructor
    }

    //Factory method to create a new instance of AdminLoginFrag using provided parameters.
    public static AdminLoginFrag newInstance(String param1, String param2) {
        AdminLoginFrag fragment = new AdminLoginFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { //Retrieving parameters passed via the arguments bundle.
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Defining UI elements in order to implement them
        MaterialToolbar backButton = view.findViewById(R.id.button_AdminLoginFrag_back);
        NavController navController = Navigation.findNavController(view);//Get the NavController to handle navigation

        //Implementing a click listener on the back button to navigate back to the user selection screen.
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_adminLoginFrag_to_loginUserSelectionFrag)
        );



    }
}
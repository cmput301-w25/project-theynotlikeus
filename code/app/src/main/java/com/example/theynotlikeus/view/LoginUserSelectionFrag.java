package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.theynotlikeus.R;

/**
 * Fragment for selecting user or admin login. It displays buttons for user and admin login.
 *
 * This version no longer checks for network connectivity or tests the Firestore connection,
 * allowing navigation to work offline.
 */
public class LoginUserSelectionFrag extends Fragment {

    public LoginUserSelectionFrag() {
        // Required empty public constructor
    }

    public static LoginUserSelectionFrag newInstance(String param1, String param2) {
        LoginUserSelectionFrag fragment = new LoginUserSelectionFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve any parameters if needed.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_user_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find the buttons for implementing navigation
        Button buttonGoToLoginUser = view.findViewById(R.id.button_LoginUserSelectionFragment_user);
        Button buttonGoToLoginAdmin = view.findViewById(R.id.button_LoginUserSelectionFragment_admin);

        // Get NavController for navigation actions
        NavController navController = Navigation.findNavController(view);

        // Set onClickListener for User button to navigate directly to the user login fragment
        buttonGoToLoginUser.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginUserSelectionFrag_to_userLoginFrag);
        });

        // Set onClickListener for Admin button to navigate directly to the admin login fragment
        buttonGoToLoginAdmin.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginUserSelectionFrag_to_adminLoginFrag);
        });
    }
}

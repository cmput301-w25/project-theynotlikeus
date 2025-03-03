package com.example.theynotlikeus;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginUserSelectionFrag#newInstance} factory method to create an instance of this fragment.
 *
 * It also:
 * - Displays the user selection screen where users choose between a User or Admin login.
 * - Navigates to the appropriate login screen based on the user's selection.
 */
public class LoginUserSelectionFrag extends Fragment {

    //Parameter argument keys for potential future customization.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Parameters that can be used to customize this fragment.
    private String mParam1;
    private String mParam2;

    public LoginUserSelectionFrag() {
        //Required empty public constructor.
    }


     //Factory method to create a new instance of LoginUserSelectionFrag using the provided parameters.
    public static LoginUserSelectionFrag newInstance(String param1, String param2) {
        LoginUserSelectionFrag fragment = new LoginUserSelectionFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_user_selection, container, false);//Inflate the layout for this fragment
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find buttons for user and admin login selections for implementation
        Button buttonGoToLoginUser = view.findViewById(R.id.button_LoginUserFrag_user);
        Button buttonGoToLoginAdmin = view.findViewById(R.id.button_LoginUserFrag_admin);

        //Get the NavController and set up navigation so the user navigate to the user login fragment.
        NavController navController = Navigation.findNavController(view);
        buttonGoToLoginUser.setOnClickListener(v ->
                navController.navigate(R.id.action_loginUserSelectionFrag_to_userLoginFrag)
        );

        //When the admin button is clicked, navigate to the admin login fragment.
        buttonGoToLoginAdmin.setOnClickListener(v ->
                navController.navigate(R.id.action_loginUserSelectionFrag_to_adminLoginFrag)
        );
    }
}

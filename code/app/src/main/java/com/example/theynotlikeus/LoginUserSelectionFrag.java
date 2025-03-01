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
 * Use the {@link LoginUserSelectionFrag#newInstance} factory method to
 * create an instance of this fragment.
 * Displays the user selection screen (User vs. Admin).
 * Navigates to the appropriate login screen.
 */
public class LoginUserSelectionFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginUserSelectionFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginUserSelection.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_user_selection, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find button
        Button buttonGoToLoginUser = view.findViewById(R.id.button_user);
        Button buttonGoToLoginAdmin = view.findViewById(R.id.button_admin);

        // Get NavController and set navigation action
        NavController navController = Navigation.findNavController(view);

        buttonGoToLoginUser.setOnClickListener(v ->
                navController.navigate(R.id.action_loginUserSelectionFrag_to_userLoginFrag)
        );

        buttonGoToLoginAdmin.setOnClickListener(v ->
                navController.navigate(R.id.action_loginUserSelectionFrag_to_adminLoginFrag)
        );
    }
}
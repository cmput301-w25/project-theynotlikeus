package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.UserController;
import com.example.theynotlikeus.model.User;
import com.google.android.material.appbar.MaterialToolbar;

public class UserSignUpFrag extends Fragment {

    public UserSignUpFrag() {
        // Required empty public constructor
    }

    public static UserSignUpFrag newInstance(String param1, String param2) {
        UserSignUpFrag fragment = new UserSignUpFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments if needed.
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_user_sign_up, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // Set up back button navigation
        MaterialToolbar backButton = view.findViewById(R.id.button_UserSignUpFrag_back);
        NavController navController = Navigation.findNavController(view);
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag)
        );

        // Get references to UI elements
        Button signInButton = view.findViewById(R.id.button_UserSignUpFrag_createandlogin);
        EditText usernameEditText = view.findViewById(R.id.editText_UserSignUpFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_UserSignUpFrag_password);
        EditText repasswordEditText = view.findViewById(R.id.editText_UserSignUpFrag_reEnterPassword);

        // Instantiate the UserController
        UserController userController = new UserController();

        signInButton.setOnClickListener(v -> {
            // Retrieve user inputs
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String repassword = repasswordEditText.getText().toString().trim();

            // Validate that the fields are not empty
            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(requireContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the passwords match
            if (!password.equals(repassword)) {
                Toast.makeText(requireContext(), "Password mismatched", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use the controller to sign up the user
            userController.signUpUser(username, password, new UserController.SignUpCallback() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.putExtra("username", user.getUsername()); // Pass username to the next activity
                    startActivity(intent);
                    requireActivity().finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    // Navigate to login if the username already exists
                    if ("Username already exists".equals(error)) {
                        navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag);
                    }
                }
            });
        });
    }
}

package com.example.theynotlikeus;

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

import com.example.theynotlikeus.controller.UserController;
import com.google.android.material.appbar.MaterialToolbar;

public class UserLoginFrag extends Fragment {

    private UserController controller;

    public UserLoginFrag() {
        //Required empty public constructor
    }

    public static UserLoginFrag newInstance(String param1, String param2) {
        UserLoginFrag fragment = new UserLoginFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new UserController();  //Initialize the controller for this fragment - UserController
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize UI elements
        MaterialToolbar backButton = view.findViewById(R.id.button_UserLoginFrag_back);
        NavController navController = Navigation.findNavController(view);
        EditText usernameEditText = view.findViewById(R.id.editText_userLoginFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_userLoginFrag_password);
        Button signInButton = view.findViewById(R.id.button_UserLogin_SignIn);

        //Handle back button navigation
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_userLoginFrag_to_loginUserSelectionFrag));

        //Navigate to the sign-up screen
        view.findViewById(R.id.textButton_UserLoginFrag_signUp).setOnClickListener(v ->
                navController.navigate(R.id.action_userLoginFrag_to_userSignUpFrag)
        );

        //Handle sign-in process after the user clicks the sign-in button
        signInButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }


            controller.loginUser(username, password, new UserController.LoginCallback() { //Call the UserController to handle login
                @Override
                public void onSuccess(User user) {//Redirect to the main activity
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.putExtra("username", user.getUsername()); //Passing the username to the next activity
                    startActivity(intent);
                    requireActivity().finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); //Prints an error message
                }
            });
        });
    }
}

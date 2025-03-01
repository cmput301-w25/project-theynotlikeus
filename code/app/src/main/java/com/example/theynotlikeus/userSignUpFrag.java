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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
/**
 * Handles user sign-up.
 * Checks if a username already exists before registering a new user.
 * Navigates to the login screen or main activity upon successful signup.
 * */
public class userSignUpFrag extends Fragment {

    //private String mParam1;
    //private String mParam2;

    public userSignUpFrag() {
        // Required empty public constructor
    }

    public static userSignUpFrag newInstance(String param1, String param2) {
        userSignUpFrag fragment = new userSignUpFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
        */

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

        // Back button navigation
        MaterialToolbar backButton = view.findViewById(R.id.button_userSignUpFrag_back);
        NavController navController = Navigation.findNavController(view);
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag)
        );

        // Get references to UI elements
        Button signInButton = view.findViewById(R.id.button_userSignUpFrag_createandlogin);
        EditText usernameEditText = view.findViewById(R.id.editText_UserSignUpFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_UserSignUpFrag_password);
        EditText repasswordEditText = view.findViewById(R.id.editText_userSignUpFrag_reEnterPassword);

        signInButton.setOnClickListener(v -> {
            // Retrieve user inputs
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String repassword = repasswordEditText.getText().toString().trim();

            // Basic validation
            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Checking if the passwords are the same or not
            if (!password.equals(repassword)) {
                Toast.makeText(getContext(), "Password mismatched lil bro", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {

                            Toast.makeText(getContext(), "User already exists. Please sign in.", Toast.LENGTH_SHORT).show();


                            navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag);
                        } else {
                            User user = new User(username, password); //enter the username and password into the database

                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();


                                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                                        intent.putExtra("username", user.getUsername());
                                        startActivity(intent);
                                        requireActivity().finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

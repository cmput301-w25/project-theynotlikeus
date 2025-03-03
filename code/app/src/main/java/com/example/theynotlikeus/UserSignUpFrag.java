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

/**
 * This fragment provides a user interface for new users to create an account.
 *
 * This fragment:
 * Checks if a username already exists before registering a new user.
 * Provides a user interface for new users to create an account.
 * Validates that required fields are filled and that the passwords match.
 * Checks for an existing username in the Firestore database.
 * If the username does not exist, registers the new user and navigates to MainActivity.
 */
public class UserSignUpFrag extends Fragment {


    //private String mParam1;
    //private String mParam2;


    public UserSignUpFrag() {
        //Required empty public constructor
    }


     //Factory method to create a new instance of userSignUpFrag.
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

        return inflater.inflate(R.layout.fragment_user_sign_up, container, false);//Inflate the layout for this fragment
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        //when the back button is clicked the user is navigated back to the user login fragment.
        MaterialToolbar backButton = view.findViewById(R.id.button_userSignUpFrag_back);
        NavController navController = Navigation.findNavController(view);
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag)
        );

        //Get references to UI elements for user input in order to implement them
        Button signInButton = view.findViewById(R.id.button_userSignUpFrag_createandlogin);
        EditText usernameEditText = view.findViewById(R.id.editText_UserSignUpFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_UserSignUpFrag_password);
        EditText repasswordEditText = view.findViewById(R.id.editText_userSignUpFrag_reEnterPassword);

        //Set click listener for the sign-up button to handle account creation.
        signInButton.setOnClickListener(v -> {
            //Retrieve user inputs and trim any extra spaces.
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String repassword = repasswordEditText.getText().toString().trim();

            //Ensure no field is empty.
            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            //Check if the two password entries match.
            if (!password.equals(repassword)) {
                Toast.makeText(getContext(), "Password mismatched lil bro", Toast.LENGTH_SHORT).show();
                return;
            }

            //Get an instance of Firestore.
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //Query the "users" collection to check if the username already exists.
            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {//If the query returns a non-empty result, the user already exists.
                            Toast.makeText(getContext(), "User already exists. Please sign in.", Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.action_userSignUpFrag_to_userLoginFrag);
                        } else {
                            //Create a new User object with the provided username and password.
                            User user = new User(username, password);

                            //Add the new user to the "users" collection in Firestore.
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();

                                        // Navigate to MainActivity after successful account creation.
                                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                                        intent.putExtra("username", user.getUsername());
                                        startActivity(intent);
                                        // Finish the current activity to prevent navigating back to the sign-up screen.
                                        requireActivity().finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        //Handle errors that occur while checking if the username exists.
                        Toast.makeText(getContext(), "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
/*
*   Fragment handling user login functionality.
*   Users can log in with their credentials, navigate to sign-up, or go back to selection.
*/

/**
 * A fragment that allows the user to login to their already existing account and select sign up if they do not have an account
 */
public class UserLoginFrag extends Fragment {

    //private String mParam1;
    //private String mParam2;

    public UserLoginFrag() {
        // Required empty public constructor
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
        //Handle back button navigation so the user can navigate to the login/signup page when clicked
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_userLoginFrag_to_loginUserSelectionFrag)
        );


        //Navigate to the sign-up screen
        view.findViewById(R.id.textButton_UserLoginFrag_signUp).setOnClickListener(v ->
                navController.navigate(R.id.action_userLoginFrag_to_userSignUpFrag)
        );
        //Handle sign-in process after the user clicks on the sign in button
        signInButton.setOnClickListener(v -> {

            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            //Authenticate user from Firestore by searching for the username entered by the user in the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {

                            Toast.makeText(getContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : querySnapshot){
                                User user = document.toObject(User.class); //converting the user to a user class upon successful log in

                                if (user.getPassword().equals(password)){
                                    //Successful login, redirect to main activity
                                    Intent intent = new Intent(requireActivity(), MainActivity.class);

                                    intent.putExtra("username", user.getUsername());//Passing the username to the next activity
                                    startActivity(intent);
                                    requireActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

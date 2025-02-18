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

import java.util.HashMap;
import java.util.Map;

public class userSignUpFrag extends Fragment {

    // Parameter names if needed (or remove if not used)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public userSignUpFrag() {
        // Required empty public constructor
    }

    public static userSignUpFrag newInstance(String param1, String param2) {
        userSignUpFrag fragment = new userSignUpFrag();
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
        return inflater.inflate(R.layout.fragment_user_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        signInButton.setOnClickListener(v -> {
            // Retrieve user inputs
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Basic validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a map to hold the user data
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("password", password);


            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {

                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(getContext(), "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

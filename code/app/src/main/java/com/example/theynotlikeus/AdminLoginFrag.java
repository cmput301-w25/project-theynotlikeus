package com.example.theynotlikeus;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        Button signInButton = view.findViewById(R.id.button_AdminLogIn_SignIn);
        EditText usernameEditText = view.findViewById(R.id.editText_adminLoginFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_adminLoginFrag_password);
        signInButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            };
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("admin")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {

                            Toast.makeText(getContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : querySnapshot){
                                User user = document.toObject(User.class);

                                if (user.getPassword().equals(password)){
                                    Intent intent = new Intent(requireActivity(), AdminActivity.class);
                                    intent.putExtra("username", user.getUsername());
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
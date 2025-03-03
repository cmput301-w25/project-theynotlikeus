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
 * Fragment for admin login.
 * Allows an admin to sign in and access the admin panel.
 * Admins can only be registered via firebase
 */
public class AdminLoginFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminLoginFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment adminLoginFrag.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
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

        // Find button
        MaterialToolbar backButton = view.findViewById(R.id.button_AdminLoginFrag_back);
        NavController navController = Navigation.findNavController(view);
        // Back button navigation

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
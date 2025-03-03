package com.example.theynotlikeus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUserSelectionFrag extends Fragment {

    public LoginUserSelectionFrag() {
        // Required empty public constructor
    }

    public static LoginUserSelectionFrag newInstance(String param1, String param2) {
        LoginUserSelectionFrag fragment = new LoginUserSelectionFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve any parameters if needed
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
        // Find the buttons
        Button buttonGoToLoginUser = view.findViewById(R.id.button_user);
        Button buttonGoToLoginAdmin = view.findViewById(R.id.button_admin);

        // Get NavController for navigation actions
        NavController navController = Navigation.findNavController(view);

        // Set onClickListener for User button with connectivity and Firestore test
        // Blocks the user navigation if network fails
        buttonGoToLoginUser.setOnClickListener(v -> {
            if (!isNetworkConnected()) {
                Toast.makeText(requireContext(),
                        "No network connection available. Try again lil bro.",
                        Toast.LENGTH_LONG).show();

                return;
            }
            // Test Firestore connection before navigating
            // change documentPath: "connectionTest" to documentPath: "NoconnectionTest"
            // to see the user blocked from logging in
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference testDocRef = db.collection("test").document("connectionTest");
            testDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // Firestore connection successful – navigate to user login fragment
                    navController.navigate(R.id.action_loginUserSelectionFrag_to_userLoginFrag);
                } else {
                    Toast.makeText(requireContext(),
                            "Error connecting to the database. Try again lil bro", Toast.LENGTH_LONG).show();
                }
            });
        });

        // Set onClickListener for Admin button with connectivity and Firestore test
        buttonGoToLoginAdmin.setOnClickListener(v -> {
            if (!isNetworkConnected()) {
                Toast.makeText(requireContext(),
                        "No network connection available. Try again lil bro.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference testDocRef = db.collection("test").document("connectionTest");
            testDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // Firestore connection successful – navigate to admin login fragment
                    navController.navigate(R.id.action_loginUserSelectionFrag_to_adminLoginFrag);
                } else {
                    Toast.makeText(requireContext(),
                            "Error connecting to the database. Try again lil bro", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // Helper method to check network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}

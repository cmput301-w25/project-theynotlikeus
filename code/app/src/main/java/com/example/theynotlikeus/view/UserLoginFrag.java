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

import com.example.theynotlikeus.singleton.MyApp;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.UserController;
import com.example.theynotlikeus.model.User;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * A fragment that allows the user to log in to their existing account provided that it exists within the Firestore database.
 * Validates the input with the Firestore.
 * Upon a successful login, the fragment stores the username in a global singleton.
 * Navigates to UserSignUpFrag if the account does not already exist in the Firestpre
 */
public class UserLoginFrag extends Fragment {

    private UserController controller;

    /**
     * Default constructor for UserLoginFrag.
     */
    public UserLoginFrag() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of UserLoginFrag with the given arguments.
     *
     * @param param1 First parameter to pass to the fragment.
     * @param param2 Second parameter to pass to the fragment.
     * @return  A new instance of UserLoginFrag.
     */
    public static UserLoginFrag newInstance(String param1, String param2) {
        UserLoginFrag fragment = new UserLoginFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the UserController and called when the fragment is created.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new UserController();  // Initialize the controller for this fragment
    }

    /**
     * Inflates the layout for this fragment. The layout has fields for username and password input.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_login, container, false);
    }

    /**
     * Called after the view has been created. This method sets up the user interface.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        MaterialToolbar backButton = view.findViewById(R.id.button_UserLoginFrag_back);
        NavController navController = Navigation.findNavController(view);
        EditText usernameEditText = view.findViewById(R.id.editText_userLoginFrag_username);
        EditText passwordEditText = view.findViewById(R.id.editText_userLoginFrag_password);
        Button signInButton = view.findViewById(R.id.button_UserLogin_SignIn);

        // Handle back button navigation
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_userLoginFrag_to_loginUserSelectionFrag));

        // Navigate to the sign-up screen
        view.findViewById(R.id.textButton_UserLoginFrag_signUp).setOnClickListener(v ->
                navController.navigate(R.id.action_userLoginFrag_to_userSignUpFrag)
        );

        // Handle sign-in process after the user clicks the sign-in button
        signInButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            controller.loginUser(username, password, new UserController.LoginCallback() {
                /**
                 * For successful login, stores the username in the global singleton.
                 * Navigates to Main Activity.
                 *
                 * @param user Logged in user object
                 */
                @Override
                public void onSuccess(User user) {
                    // Store the logged-in username in the global singleton (MyApp)
                    MyApp app = (MyApp) requireActivity().getApplicationContext();
                    app.setUsername(user.getUsername());

                    // Redirect to the MainActivity and pass the username along (current logic remains)
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.putExtra("username", user.getUsername());
                    startActivity(intent);
                    requireActivity().finish();
                }

                /**
                 * For failed login, displays an error message in a toast.
                 *
                 * @param error Error message
                 */
                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

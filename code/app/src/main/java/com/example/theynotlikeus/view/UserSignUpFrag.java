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
 * A fragment that handles the user sign-up process, including collecting the user's input
 * (username, password, and re-entered password), validating the input, and using the UserController to create a new user account.
 *
 * If the account creation is successful, the fragment stores the user's username in a global singleton
 * and navigates the user to the main activity. In case of an error (example, username already exists),
 * a message is displayed, and the user is navigated to the login screen.
 */
public class UserSignUpFrag extends Fragment {

    /**
     * Default constructor for UserSignUpFrag.
     */
    public UserSignUpFrag() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of UserSignUpFrag
     *
     * @param param1 First parameter to pass to the fragment.
     * @param param2 Second parameter to pass to the fragment.
     * @return A new instance of UserSignUpFrag.
     */
    public static UserSignUpFrag newInstance(String param1, String param2) {
        UserSignUpFrag fragment = new UserSignUpFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * For when the fragment is initially created.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments if needed.
    }

    /**
     * Inflates the layout for the fragment, which includes fields for entering the username,
     * password, and confirming the password.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The inflated view for the fragment.
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_user_sign_up, container, false);
    }

    /**
     * Called after the view has been created. This method sets up the user interface.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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
                /**
                 * For successful user sign up. Stores username in the global singleton.
                 *
                 * @param user Newly created user.
                 */
                @Override
                public void onSuccess(User user) {
                    // Update the global singleton with the logged-in username
                    MyApp app = (MyApp) requireActivity().getApplicationContext();
                    app.setUsername(user.getUsername());

                    Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.putExtra("username", user.getUsername()); // Pass username to the next activity
                    startActivity(intent);
                    requireActivity().finish();
                }

                /**
                 * For error during sign up. Handles error during sign up.
                 *
                 * @param error
                 */
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

package com.example.theynotlikeus.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.SearchUserAdapter;
import com.example.theynotlikeus.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.search.SearchBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays a searchable list of all users except themselves
 * It also allows the users to :
 * - Filter users in real-time by typing in a SearchView
 * - Click on a user to navigate to their profile to follow them
 */

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private com.google.android.material.search.SearchView searchView;
    private SearchBar searchBar;
    private MaterialToolbar actionBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        /* Adding back button to appbar from: https://www.geeksforgeeks.org/how-to-add-and-customize-back-button-of-action-bar-in-android/
         * Author: GeeksForGeeks
         * Taken by: Ercel Angeles
         * Taken on: March 25, 2025
         */
        // Get the MaterialToolbar from the layout.
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set a click listener for the back button.
        toolbar.setNavigationOnClickListener(v -> finish());

        // Modify position of back button
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof android.widget.ImageButton) {
                android.widget.ImageButton navButton = (android.widget.ImageButton) toolbar.getChildAt(i);
                MaterialToolbar.LayoutParams params = (MaterialToolbar.LayoutParams) navButton.getLayoutParams();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                navButton.setLayoutParams(params);
                break;
            }
        }

        // Change the back button color to white.
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(
                    ContextCompat.getColor(this, R.color.white)
            );
        }

        //Initialize UI components
        recyclerView = findViewById(R.id.recyclerview_SearchUserActivity);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize data structures and adapter
        userList = new ArrayList<>();
        adapter = new SearchUserAdapter(this, userList, this::OnUserClick);
        recyclerView.setAdapter(adapter);

        //Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Load user list from Firestore
        loadUsers();

        /* Code for search material UI from: https://github.com/material-components/material-components-android/blob/master/docs/components/Search.md#search-view
         * Author: imhappi, paulfthomas
         * Taken by: Ercel Angeles
         * Taken on: March 25, 2025
         */


        TextInputEditText searchEditText = findViewById(R.id.search_edit_text);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }






    /**
     * Callback triggered when a user is clicked in the list.
     */
    private void OnUserClick(User user) {
        openUserProfile(user);
    }

    /**
     * Loads all users from Firestore, excluding the currently logged-in user.
     */
    private void loadUsers() {
        String loggedInUsername = getIntent().getStringExtra("username");

        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);

                        //Exclude the logged-in user from search results
                        if (!user.getUsername().equals(loggedInUsername)) {
                            userList.add(user);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show());
    }

    /**
     * Filters the user list based on the search query.
     * If the query is the logged-in user's name, the list is cleared.
     */
    private void filterUsers(String query) {
        String loggedInUsername = getIntent().getStringExtra("username");
        List<User> filteredList = new ArrayList<>();

        //Prevent showing self in filtered results
        if (query.equalsIgnoreCase(loggedInUsername)) {
            adapter.updateList(filteredList);
            return;
        }

        // Filter users by partial match on username
        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        adapter.updateList(filteredList);
    }






    /**
     * Navigates to the selected user's profile.
     * Passes both the viewed and logged-in usernames via Intent.
     */
    private void openUserProfile(User user) {
        Intent intent = new Intent(this, ViewUserProfileActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("loggedInUser", getIntent().getStringExtra("username"));
        startActivity(intent);
    }
}

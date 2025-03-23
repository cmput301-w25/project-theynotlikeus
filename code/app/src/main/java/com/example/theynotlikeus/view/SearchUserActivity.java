package com.example.theynotlikeus.view;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.SearchUserAdapter;
import com.example.theynotlikeus.model.User;
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
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_user);

        //Initialize UI components
        recyclerView = findViewById(R.id.recyclerview_SearchUserFrag);
        searchView = findViewById(R.id.searchView_SearchUserFrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize data structures and adapter
        userList = new ArrayList<>();
        adapter = new SearchUserAdapter(this, userList, this::OnUserClick);
        recyclerView.setAdapter(adapter);

        //Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Load user list from Firestore
        loadUsers();

        //Ensure SearchView expands on click
        searchView.setOnClickListener(v -> searchView.setIconified(false));

        //Handle back button
        findViewById(R.id.button_SearchUserFrag_backbutton).setOnClickListener(v -> finish());

        //Handle text input for filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
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

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

        recyclerView = findViewById(R.id.recyclerview_SearchUserFrag);
        searchView = findViewById(R.id.searchView_SearchUserFrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new SearchUserAdapter(this, userList, this::OnUserClick);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadUsers();

        findViewById(R.id.button_SearchUserFrag_backbutton).setOnClickListener(v -> finish());

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

    private void OnUserClick(User user) {
        openUserProfile(user);  // Navigate to user profile
    }


    private void loadUsers() {
        String loggedInUsername = getIntent().getStringExtra("username");
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            userList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                User user = doc.toObject(User.class);
                if (!user.getUsername().equals(loggedInUsername)) {
                    userList.add(user);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show());
    }

    private void filterUsers(String query) {
        String loggedInUsername = getIntent().getStringExtra("username");
        List<User> filteredList = new ArrayList<>();


        if (query.equalsIgnoreCase(loggedInUsername)) {
            adapter.updateList(filteredList);
            return;
        }

        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private void openUserProfile(User user) {
        Intent intent = new Intent(this, ViewUserProfileActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("loggedInUser", getIntent().getStringExtra("username")); // Pass the logged-in user's username
        startActivity(intent);
    }
}

package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.FollowerRecyclerViewAdapter;
import com.example.theynotlikeus.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FollowerRequestFrag extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<User> userList;
    private FollowerRecyclerViewAdapter adapter;

    public FollowerRequestFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and return the layout for this fragment.
        return inflater.inflate(R.layout.fragment_follower_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview_FollowerRequestFrag_followerrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        adapter = new FollowerRecyclerViewAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // First try to get the user from FirebaseAuth
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Use the UID from FirebaseAuth
            loadFollowRequests(firebaseUser.getUid());
        } else {
            // Fallback: get the username passed from MainActivity
            String username = requireActivity().getIntent().getStringExtra("username");
            if (username != null && !username.isEmpty()) {
                loadFollowRequests(username);
            } else {
                Toast.makeText(getContext(), "User not authenticated. Please login.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Query Firestore for follow requests where the current user is the followee.
     * @param currentUserId The UID or username of the current user.
     */
    private void loadFollowRequests(String currentUserId) {
        db.collection("request")
                .whereEqualTo("followee", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No follow requests found", Toast.LENGTH_SHORT).show();
                    } else {
                        // For each request, extract the follower's username and add to the list.
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String followerUsername = doc.getString("follower");
                            if (followerUsername != null) {
                                User user = new User();
                                user.setUsername(followerUsername);
                                userList.add(user);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error fetching follow requests", Toast.LENGTH_SHORT).show()
                );
    }
}

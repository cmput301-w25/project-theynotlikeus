package com.example.theynotlikeus.view;

import android.content.Intent;
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
import com.example.theynotlikeus.controller.FollowRequestController;
import com.example.theynotlikeus.model.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FollowerRequestFrag extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Request> requestList;
    private FollowerRecyclerViewAdapter adapter;
    private FollowRequestController followRequestController;

    public FollowerRequestFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        followRequestController = new FollowRequestController();
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
        requestList = new ArrayList<>();
        adapter = new FollowerRecyclerViewAdapter(getContext(), requestList, followRequestController);
        recyclerView.setAdapter(adapter);

        // Retrieve username from the activity's intent
        String username = requireActivity().getIntent().getStringExtra("username");

        // Set up the Floating Action Button to launch SearchUserFrag as a fragment
        FloatingActionButton fabAddFollow = view.findViewById(R.id.floatingActionButton_FollowerRequestFrag_addfollow);
        fabAddFollow.setOnClickListener(v -> {
            if (username != null && !username.isEmpty()) {
                // Start SearchUserActivity instead of treating it like a Fragment
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "User not authenticated. Please login.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Retrieve the username passed via the Intent from MainActivity.
        String username = requireActivity().getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            loadFollowRequests(username);
        } else {
            Toast.makeText(getContext(), "User not authenticated. Please login.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Query Firestore for follow requests where the current user is the followee.
     * @param currentUserId The username of the current user.
     */
    private void loadFollowRequests(String currentUserId) {
        db.collection("request")
                .whereEqualTo("followee", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No follow requests found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String followerUsername = doc.getString("follower");
                            String followee = doc.getString("followee");
                            if (followerUsername != null) {
                                Request request = new Request();
                                request.setId(doc.getId());
                                request.setFollower(followerUsername);
                                request.setFollowee(followee);
                                requestList.add(request);
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

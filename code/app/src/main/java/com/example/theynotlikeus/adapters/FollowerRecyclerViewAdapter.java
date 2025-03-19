package com.example.theynotlikeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying a list of users requesting to follow.
 */
public class FollowerRecyclerViewAdapter extends RecyclerView.Adapter<FollowerRecyclerViewAdapter.MyViewHolder>{
    private List<User> userList;
    private Context context;
    private FirebaseFirestore db;
    public FollowerRecyclerViewAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_follower_layout, parent, false);
        return new FollowerRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUsername.setText(user.getUsername());
        holder.buttonAcceptRequest.setOnClickListener(v -> acceptRequest(user));
        holder.buttonDeclineRequest.setOnClickListener(v -> declineRequest(user));

    }

    private void acceptRequest(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String requestId = user.getUsername(); // Assume username is unique for now

        // Move request to 'follow' collection and remove from 'request' collection
        Map<String, Object> followData = new HashMap<>();
        followData.put("followed", FirebaseAuth.getInstance().getCurrentUser().getUid());
        followData.put("follower", user.getUsername());

        db.collection("follow").add(followData)
                .addOnSuccessListener(documentReference -> {
                    db.collection("request").whereEqualTo("follower", user.getUsername())
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    db.collection("request").document(doc.getId()).delete();
                                }
                            });
                });
    }

    private void declineRequest(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("request").whereEqualTo("follower", user.getUsername())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        db.collection("request").document(doc.getId()).delete();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        Button buttonAcceptRequest;
        Button buttonDeclineRequest;

        /**
         * Constructor for initializing UI elements in the ViewHolder.
         *
         * @param itemView The view representing a single user.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textview_FragmentFollowerLayout_username);
            buttonAcceptRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_acceptButton);
            buttonDeclineRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_declineButton);
        }
    }
}

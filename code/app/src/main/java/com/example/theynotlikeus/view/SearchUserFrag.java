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
import com.example.theynotlikeus.adapters.SearchUserAdapter;
import com.example.theynotlikeus.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchUserFrag extends Fragment {

    private RecyclerView recyclerView;
    private SearchUserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview_SearchUserFrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        //adapter = new SearchUserAdapter(getContext(), userList, this::openUserProfile);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            fetchAllUsers();
        }
    }

    private void fetchAllUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    userList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String userId = doc.getId();
                        String username = doc.getString("username");
                        userList.add(new User(userId, username));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error fetching users", Toast.LENGTH_SHORT).show()
                );
    }

//    private void openUserProfile(User user) {
//        Bundle bundle = new Bundle();
//        bundle.putString("username", user.getUsername());
//
//        UserProfileFrag userProfileFrag = new UserProfileFrag();
//        userProfileFrag.setArguments(bundle);
//
//        getParentFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, userProfileFrag)
//                .addToBackStack(null)
//                .commit();
//    }
}
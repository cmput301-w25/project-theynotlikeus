package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class homeMyMoodsFrag extends Fragment {

    private String username;
    private List<Mood> userMoodList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    private FirebaseFirestore db;
    private CollectionReference moodListRef;

    // current filter values
    private boolean filterRecentweek = false;
    private String filterEmotionalstate = "All Moods";
    private String filterReason = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        // Retrieve the username from the Activity's Intent extras
        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }

        // Set the welcome message with the username
        TextView usernameTextView = view.findViewById(R.id.textView_homeMyMoodFrag_welcomeUser);
        if (username != null) {
            usernameTextView.setText("Welcome, " + username + "!");
        } else {
            usernameTextView.setText("Welcome!");
        }

        // Handle Floating Action Button (FAB) click to add a new mood
        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_homeMyMoodsFrag_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Handle profile picture click to go to profile activity
        ImageView profileImage = view.findViewById(R.id.ImageView_homeMyMoodsFrag_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        // Setup AutoCompleteTextView for emotional state filtering
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                filterOptions
        );
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

        // Setup SearchView for filtering by reason text
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterReason = query;
                loadMoodsFromFirebase();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterReason = newText;
                loadMoodsFromFirebase();
                return false;
            }
        });

        // Setup RecyclerView
        userRecyclerView = view.findViewById(R.id.recyclerview_fragmenthomemymoods_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        moodListRef = db.collection("moods");

        // Refresh data when fragment is created
        loadMoodsFromFirebase();

        // Setup CheckBox for filtering by recent week
        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_recentWeek);
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentweek = isChecked;
            loadMoodsFromFirebase();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the mood list every time the fragment is resumed
        loadMoodsFromFirebase();
    }

    private void loadMoodsFromFirebase() {
        Log.d("UserProfile", "Loading moods for username: '" + username + "'");

        // Build query: moods for the given username, ordered by dateTime descending
        Query query = moodListRef
                .whereEqualTo("username", username)
                .orderBy("dateTime", Query.Direction.DESCENDING);

        // Apply recent week filter if enabled
        if (filterRecentweek) {
            long recentWeekMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            Timestamp recentWeekTimestamp = new Timestamp(new Date(recentWeekMillis));
            query = query.whereGreaterThanOrEqualTo("dateTime", recentWeekTimestamp);
        }

        // Apply emotional state filter if not "All Moods"
        if (filterEmotionalstate != null && !filterEmotionalstate.equals("All Moods") && !filterEmotionalstate.isEmpty()) {
            String filterValue = filterEmotionalstate.toUpperCase();
            query = query.whereEqualTo("moodState", filterValue);
        }

        // Execute the query (one-time read)
        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firestore", "Error fetching moods", task.getException());
                return;
            }

            List<Mood> moods = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                Log.d("Firestore", "Document ID: " + snapshot.getId() + ", Data: " + snapshot.getData());
                Mood mood = snapshot.toObject(Mood.class);
                // Set default mood state if null
                if (mood.getMoodState() == null) {
                    mood.setMoodState(Mood.MoodState.HAPPINESS);
                }
                moods.add(mood);
            }

            // Apply local reason filter if provided
            if (filterReason != null && !filterReason.isEmpty()) {
                List<Mood> filteredList = new ArrayList<>();
                for (Mood mood : moods) {
                    if (mood.getReason() != null &&
                            mood.getReason().toLowerCase().contains(filterReason.toLowerCase())) {
                        filteredList.add(mood);
                    }
                }
                moods = filteredList;
            }

            userMoodList.clear();
            userMoodList.addAll(moods);
            userRecyclerViewAdapter.notifyDataSetChanged();
            // log for testing
            Log.d("Firestore", "Total Moods Fetched: " + userMoodList.size());
        });
    }
}

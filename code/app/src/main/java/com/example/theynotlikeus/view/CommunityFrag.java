package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.CommunityRecyclerViewAdapter;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityFrag extends Fragment {

    private static final String TAG = "CommunityFrag";

    // Views
    private RecyclerView communityRecyclerView;
    private CheckBox recentWeekCheckBox;
    private AutoCompleteTextView communityAutoCompleteTextView;
    private SearchView searchViewCommunity;

    // Data
    private List<Mood> allCommunityMoods = new ArrayList<>();  // Moods from friends only.
    private List<Mood> filteredMoods = new ArrayList<>();        // Filtered list for the adapter.
    private CommunityRecyclerViewAdapter communityAdapter;

    // Current filter states.
    private boolean filterRecentWeek = false;
    private String filterEmotionalState = "All Moods";
    private String filterTriggerText = "";

    // Firestore instance.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommunityFrag() {
        // Required empty constructor.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Find views.
        communityRecyclerView = view.findViewById(R.id.recyclerview_CommunityFrag_users);
        recentWeekCheckBox = view.findViewById(R.id.checkBox_CommunityFrag_recentWeek);
        searchViewCommunity = view.findViewById(R.id.searchView_CommunityFrag);
        TextInputLayout dropdownLayout = view.findViewById(R.id.community_dropdown_menu_layout);
        communityAutoCompleteTextView = view.findViewById(R.id.community_autoCompleteTextView);

        // 2) Setup RecyclerView + adapter.
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        communityAdapter = new CommunityRecyclerViewAdapter(filteredMoods);
        communityRecyclerView.setAdapter(communityAdapter);

        // 3) Setup autoCompleteTextView (emotional states).
        String[] filterOptions = {
                "All Moods", "HAPPINESS", "SADNESS", "ANGER", "SURPRISE", "FEAR", "DISGUST", "SHAME", "CONFUSION"
        };
        android.widget.ArrayAdapter<String> moodStatesAdapter =
                new android.widget.ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        filterOptions
                );
        communityAutoCompleteTextView.setAdapter(moodStatesAdapter);
        communityAutoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalState = (String) parent.getItemAtPosition(position);
            applyFilters();
        });

        // 4) Setup SearchView for triggers.
        searchViewCommunity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTriggerText = query;
                applyFilters();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterTriggerText = newText;
                applyFilters();
                return true;
            }
        });

        // 5) Setup checkBox for "recent week".
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentWeek = isChecked;
            applyFilters();
        });

        // 6) Load moods from friends only.
        loadFriendsMoods();
    }

    /**
     * Loads the current user's friend list from Firestore and, for each friend,
     * retrieves their **public** mood events using the MoodController.
     */
    private void loadFriendsMoods() {
        String currentUser = requireActivity().getIntent().getStringExtra("username");

        // Query the "follow" collection for documents where the current user is the follower.
        db.collection("follow")
                .whereEqualTo("follower", currentUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> friendList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String friend = doc.getString("followee");
                        if (friend != null && !friend.isEmpty()) {
                            friendList.add(friend);
                        }
                    }
                    Log.d(TAG, "Found friends: " + friendList.toString());
                    // For each friend, fetch their **public** moods.
                    MoodController moodController = new MoodController();
                    for (String friend : friendList) {
                        moodController.getPublicMoodsByUser(friend,
                                moods -> {
                                    allCommunityMoods.addAll(moods);
                                    applyFilters();
                                },
                                e -> Log.e(TAG, "Error fetching moods for friend " + friend + ": " + e.getMessage())
                        );
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching friend list: " + e.getMessage()));
    }


    /**
     * Filters and sorts the mood list from friends, then refreshes the adapter.
     */
    private void applyFilters() {
        Log.d(TAG, "Applying filters: recentWeek=" + filterRecentWeek
                + ", emotionalState=" + filterEmotionalState
                + ", triggerText=" + filterTriggerText);

        filteredMoods.clear();

        // Filter logic.
        long oneWeekAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        for (Mood mood : allCommunityMoods) {
            boolean include = true;

            // Filter by "recent week" if checkbox is checked.
            if (filterRecentWeek) {
                if (mood.getDateTime() == null || mood.getDateTime().getTime() < oneWeekAgoMillis) {
                    include = false;
                }
            }

            // Filter by emotional state.
            if (!TextUtils.isEmpty(filterEmotionalState) &&
                    !"All Moods".equalsIgnoreCase(filterEmotionalState)) {
                if (!mood.getMoodState().name().equalsIgnoreCase(filterEmotionalState)) {
                    include = false;
                }
            }

            // Filter by trigger text.
            if (!TextUtils.isEmpty(filterTriggerText)) {
                if (mood.getTrigger() == null ||
                        !mood.getTrigger().toLowerCase().contains(filterTriggerText.toLowerCase())) {
                    include = false;
                }
            }

            if (include) {
                filteredMoods.add(mood);
            }
        }

        // Sort in reverse chronological order (most recent first).
        Collections.sort(filteredMoods, (m1, m2) ->
                m2.getDateTime().compareTo(m1.getDateTime()));

        // Update adapter.
        communityAdapter.notifyDataSetChanged();
    }
}

package com.example.theynotlikeus.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.CommunityRecyclerViewAdapter;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment for the community page where the user can see and filter mood events of users whom they follow with:
 * - a dropdown menu with mood filter options
 * - a search box to search for a given word within the reason
 * - a checkbox for the recent week filter
 */
public class CommunityFrag extends Fragment {

    private static final String TAG = "CommunityFrag";

    // Views
    private RecyclerView communityRecyclerView;
    private CheckBox recentWeekCheckBox;
    private MaterialAutoCompleteTextView communityAutoCompleteTextView;
    private TextInputEditText searchEditText;

    // Data lists.
    private List<Mood> allCommunityMoods = new ArrayList<>();  // Cached moods from friends.
    private List<Mood> filteredMoods = new ArrayList<>();        // Filtered moods for adapter.
    private CommunityRecyclerViewAdapter communityAdapter;

    // Filter states.
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
        communityAutoCompleteTextView = view.findViewById(R.id.community_autoCompleteTextView);
        searchEditText = view.findViewById(R.id.community_search_edit_text);

        // 2) Setup RecyclerView and its adapter.
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        communityAdapter = new CommunityRecyclerViewAdapter(filteredMoods);
        communityRecyclerView.setAdapter(communityAdapter);

        // 3) Setup autoCompleteTextView (emotional states).
        MaterialAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.community_autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame", "Confusion"};
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        communityAutoCompleteTextView.setAdapter(autoAdapter);
        communityAutoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalState = parent.getItemAtPosition(position).toString();
            applyFilters();
        });
        // Show dropdown when clicked or focused
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown();
            }
        });
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

        // 4) Setup searchEditText for trigger filtering.
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterTriggerText = searchEditText.getText().toString().trim();
                applyFilters();
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTriggerText = s.toString().trim();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 5) Setup checkBox for "recent week".
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentWeek = isChecked;
            applyFilters();
        });

        // 6) Load moods from friends.
        loadFriendsMoods();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Loads the current user's friend list and, for each friend, retrieves their mood events.
     * For each friend, only the top 3 most recent moods are considered.
     * Only moods that are public and approved (pendingReview == false) are added.
     */
    private void loadFriendsMoods() {
        // Clear the local cache so that deletions and updates are reflected.
        allCommunityMoods.clear();
        String currentUser = requireActivity().getIntent().getStringExtra("username");

        db.collection("follow")
                .whereEqualTo("follower", currentUser)
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    List<String> friendList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String friend = doc.getString("followee");
                        if (friend != null && !friend.isEmpty()) {
                            friendList.add(friend);
                        }
                    }
                    Log.d(TAG, "Found friends: " + friendList.toString());
                    // For each friend, fetch their moods.
                    MoodController moodController = new MoodController();
                    for (String friend : friendList) {
                        moodController.getMoodsByUser(friend,
                                moods -> {
                                    if (moods == null || moods.isEmpty()) {
                                        Log.d(TAG, "No moods for friend: " + friend);
                                        return;
                                    }
                                    // Sort moods in descending order.
                                    Collections.sort(moods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
                                    // Keep top 3 most recent moods.
                                    List<Mood> topMoods = moods.size() > 3 ? moods.subList(0, 3) : moods;
                                    for (Mood mood : topMoods) {
                                        // Only add moods that are public and approved.
                                        if (mood.isPublic() && !mood.isPendingReview()) {
                                            allCommunityMoods.add(mood);
                                        } else {
                                            Log.d(TAG, "Skipping mood for friend " + friend + " because it's not approved.");
                                        }
                                    }
                                    applyFilters();
                                },
                                e -> Log.e(TAG, "Error fetching moods for friend " + friend + ": " + e.getMessage())
                        );
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching friend list: " + e.getMessage()));
    }

    /**
     * Applies filters on the cached list of moods and updates the adapter.
     */
    private void applyFilters() {
        Log.d(TAG, "Applying filters: recentWeek=" + filterRecentWeek
                + ", emotionalState=" + filterEmotionalState
                + ", triggerText=" + filterTriggerText);

        filteredMoods.clear();

        // Filter logic.
        long oneWeekAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        for (Mood mood : allCommunityMoods) {
            // Only include approved moods (pendingReview must be false).
            if (mood.isPendingReview()) {
                continue;
            }
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

        Collections.sort(filteredMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));

        // Update adapter on the UI thread.
        communityRecyclerView.post(() -> communityAdapter.notifyDataSetChanged());
    }
}

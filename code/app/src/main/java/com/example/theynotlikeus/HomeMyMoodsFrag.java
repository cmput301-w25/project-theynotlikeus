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
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.controller.MoodController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A fragment that displays the logged-in user's mood events.
 *
 * This fragment loads mood events from Firebase Firestore and allows the user to filter them by:
 * - Time (recent week)
 * - Emotional state
 * - Trigger keywords
 *
 * It also provides functionality to navigate to add new mood events and view personal profile details.
 */
public class HomeMyMoodsFrag extends Fragment {

    // Username of the logged-in user.
    private String username;
    // List to store user moods retrieved from Firestore.
    private List<Mood> userMoodList = new ArrayList<>();
    // RecyclerView components for displaying moods.
    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    // Filters for displaying moods.
    private boolean filterRecentweek = false;
    private String filterEmotionalstate = "All Moods";
    private String filterTrigger = "";

    // Custom MoodController to handle Firestore operations.
    private MoodController moodController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        // Retrieve the username from the Intent.
        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }
        if (username == null || username.isEmpty()) {
            Log.e("HomeMyMoodsFrag", "Username not provided. Using default username.");
            Toast.makeText(getContext(), "Username not provided. Using default user.", Toast.LENGTH_SHORT).show();
            username = "defaultUser";
        }

        // Set welcome message.
        TextView usernameTextView = view.findViewById(R.id.textView_HomeMyMoodsFragment_welcomeUser);
        usernameTextView.setText("Welcome, " + username + "!");

        // Floating action button to add a new mood event.
        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_HomeMyMoodsFragment_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // ImageView that navigates to the user's profile details.
        ImageView profileImage = view.findViewById(R.id.ImageView_HomeMyMoodsFragment_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        // Dropdown menu to filter moods by emotional state.
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

        // SearchView to filter moods by trigger words.
        SearchView searchView = view.findViewById(R.id.searchView_HomeMyMoodsFragment);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTrigger = query;
                loadMoodsFromFirebase();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterTrigger = newText;
                loadMoodsFromFirebase();
                return true;
            }
        });

        // Initialize and set up the RecyclerView.
        userRecyclerView = view.findViewById(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        // Handle mood item clicks to view details.
        userRecyclerViewAdapter.setOnItemClickListener(mood -> {
            Intent intent = new Intent(getActivity(), MoodEventDetailsActivity.class);
            intent.putExtra("moodId", mood.getDocId());
            startActivity(intent);
        });

        // Instantiate the MoodController.
        moodController = new MoodController();

        // Load moods from Firestore.
        loadMoodsFromFirebase();

        // Checkbox to filter moods from the recent week.
        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_HomeMyMoodsFragment_recentWeek);
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentweek = isChecked;
            loadMoodsFromFirebase();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMoodsFromFirebase();
    }

    /**
     * Retrieves and filters the user's moods using MoodController.
     */
    private void loadMoodsFromFirebase() {
        Log.d("HomeMyMoodsFrag", "Loading moods for username: '" + username + "'");
        moodController.getMoodsByUser(username,
                moods -> {
                    List<Mood> filteredMoods = new ArrayList<>();

                    // Apply filters to the list.
                    for (Mood mood : moods) {
                        boolean includeMood = true;

                        // Filter by recent week if enabled.
                        if (filterRecentweek) {
                            long oneWeekAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
                            Date moodDate = mood.getDateTime();
                            if (moodDate == null || moodDate.getTime() < oneWeekAgoMillis) {
                                includeMood = false;
                            }
                        }

                        // Filter by emotional state if selected.
                        if (filterEmotionalstate != null &&
                                !filterEmotionalstate.equals("All Moods") &&
                                !filterEmotionalstate.isEmpty()) {
                            // Assuming moodState is stored as an enum.
                            String moodState = mood.getMoodState().name();
                            if (!moodState.equalsIgnoreCase(filterEmotionalstate)) {
                                includeMood = false;
                            }
                        }

                        // Filter by trigger words.
                        if (filterTrigger != null && !filterTrigger.isEmpty()) {
                            if (mood.getTrigger() == null ||
                                    !mood.getTrigger().toLowerCase().contains(filterTrigger.toLowerCase())) {
                                includeMood = false;
                            }
                        }

                        if (includeMood) {
                            filteredMoods.add(mood);
                        }
                    }

                    // Sort moods by dateTime descending.
                    Collections.sort(filteredMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));

                    // Update the RecyclerView.
                    userMoodList.clear();
                    userMoodList.addAll(filteredMoods);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> userRecyclerViewAdapter.notifyDataSetChanged());
                    }
                    Log.d("HomeMyMoodsFrag", "Total moods fetched: " + userMoodList.size());
                },
                exception -> {
                    Log.e("HomeMyMoodsFrag", "Error fetching moods", exception);
                });
    }
}

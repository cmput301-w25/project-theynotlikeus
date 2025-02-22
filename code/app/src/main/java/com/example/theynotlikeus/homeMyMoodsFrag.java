package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

public class homeMyMoodsFrag extends Fragment {

    private String username;

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

        // Handle Floating Action Button (FAB) click
        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_homeMyMoodsFrag_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MoodEventActivity.class);
            startActivity(intent);
        });

        // Handle profile picture click to go to profile activity
        ImageView profileImage = view.findViewById(R.id.ImageView_homeMyMoodsFrag_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        // Set up the AutoCompleteTextView for emotional state filtering
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                filterOptions
        );
        autoCompleteTextView.setAdapter(adapter);

        // Set up SearchView for filtering by "reason why" text
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: Filter mood events based on the entered text (reason why)
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: Update filtering as text changes if desired
                return false;
            }
        });

        // Add a CheckBox for filtering by recent week (time filter subgroup)
        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_recentWeek);
        recentWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO: Filter mood events to include only those from the most recent week if isChecked is true.
            }
        });

        return view;
    }
}

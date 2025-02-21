package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

public class homeMyMoodsFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

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

        // Handle drop-down menu (filter)
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);

        // Define filter options
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};

        // Set up an ArrayAdapter with the options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,  // Pre-built drop-down item layout
                filterOptions
        );

        // Attach the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);

        return view;
    }
}

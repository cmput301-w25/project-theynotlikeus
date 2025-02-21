package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class homeMyMoodsFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        // Fix: Use view.findViewById to get the button
        ImageButton addMoodButton = view.findViewById(R.id.floatingActionButton_homeMyMoodsFrag_addmood);
        addMoodButton.setOnClickListener(v -> {
            // Fix: Use getActivity() as context
            Intent intent = new Intent(getActivity(), MoodEventActivity.class);
            startActivity(intent);
        });

        // Find profile image and set click listener
        ImageView profileImage = view.findViewById(R.id.ImageView_homeMyMoodsFrag_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}

package com.example.theynotlikeus;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class homeMyMoodsFrag extends Fragment {

    private String username;
    private List<Mood> userMoodList = new ArrayList<Mood>();
    private RecyclerView userRecyclerView;
    private RecyclerView.Adapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    private FirebaseFirestore db;
    private CollectionReference moodListRef;
    private RecyclerView recyclerView;


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
        //userRecyclerView
        userRecyclerView = view.findViewById(R.id.recyclerview_fragmenthomemymoods_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);


        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);


        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);

        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        // load data
        db = FirebaseFirestore.getInstance();
        moodListRef = db.collection("moods");
        loadMoodsFromFirebase();




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



    // ***EDIT***
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        userRecyclerView = view.findViewById(R.id.recyclerview_fragmenthomemymoods_userrecyclerview);
//        userRecyclerView.setHasFixedSize(true);
//        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
//        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
//        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
//        userRecyclerView.setAdapter(userRecyclerViewAdapter);
//
//        db = FirebaseFirestore.getInstance();
//        moodListRef = db.collection("moods");
//
//        // Log.d messages are still to be added to see if it was successful.
//        moodListRef.addSnapshotListener((value, error) -> {
//            if (error != null) {
//                Log.e("Firestore", error.toString());
//            }
//            if (value != null) {
//                userMoodList.clear();
//                for (QueryDocumentSnapshot snapshot : value) {
//                    //Timestamp timestamp = snapshot.getTimestamp("dateTime");
//                    String selectedMoodState = snapshot.getString("moodState");
//                    Mood.MoodState moodState;
//                    if (selectedMoodState != null) {
//                        moodState = Mood.MoodState.valueOf(selectedMoodState);
//                    } else {
//                        // Use a default mood state if none is provided
//                        moodState = Mood.MoodState.ANGER; // Make sure DEFAULT is defined in your enum
//                        Log.w("Firestore", "moodState field is null, using default.");
//                    }
//                    /*
//                    if (timestamp != null) {
//                        Date date = timestamp.toDate();
//                        Log.d("Firestore", "Date: " + date.toString());
//                    }
//
//                     */
//                    moodListRef.add(new Mood(moodState));
//                }
//                userRecyclerViewAdapter.notifyDataSetChanged();
//            }
//        });
//    }


    private void loadMoodsFromFirebase() {
        moodListRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firestore", "Error fetching moods", task.getException());
                return;
            }

            userMoodList.clear();

            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                Mood mood = snapshot.toObject(Mood.class);
                // fetch mood

                if (mood.getMoodState() == null) {
                    mood.setMoodState(Mood.MoodState.HAPPINESS);
                    //default as Happy
                }

                userMoodList.add(mood);
            }

//            userRecyclerViewAdapter.notifyDataSetChanged();
        });
    }// MoodEvent RecyclerView








        /*
        DocumentReference docRef = moodListRef.document("111");

        // Replace the below code with addSnapshotListener from Lab 5
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String emotionalState = document.getString("EmotionalState");
                        String userID = "ee";
                        String moodEventID = "EE";
                        String date = "feb 19";
                        String time = "5:59 PM";
                        userMoodList.add(new Mood(userID, moodEventID, date, time, emotionalState, null, null, null, null, null));
                        userRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore","get failed with ", task.getException());
                }
            }
        });

         */

}

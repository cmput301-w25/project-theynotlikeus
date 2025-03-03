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

/**
 * Fragment that displays a list of mood events for the logged-in user.
 *
 * It also:
 * Retrieves mood events from Firestore filtered by username and other criteria.
 * Provides filtering options including recent week, emotional state, and trigger text.
 * Displays mood events in a RecyclerView, allowing users to click on an item for more details.
 */
public class HomeMyMoodsFrag extends Fragment {


    private String username; //Stores the currently logged-in user's username.
    private List<Mood> userMoodList = new ArrayList<>(); //List of Mood objects to be displayed in the RecyclerView.
    private RecyclerView userRecyclerView; //RecyclerView for displaying the list of mood events.
    private UserRecyclerViewAdapter userRecyclerViewAdapter; //Adapter to bind Mood data to the RecyclerView.
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager; //LayoutManager for the RecyclerView.
    private FirebaseFirestore db; //Firestore database instance.
    private CollectionReference moodListRef; //Reference to the "moods" collection in Firestore.
    private boolean filterRecentweek = false; //Filter to limit moods to those within the recent week.
    private String filterEmotionalstate = "All Moods"; //Filter for the emotional state of moods; default is "All Moods".
    private String filterTrigger = ""; //Filter for trigger text input by the user.


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);
        //Retrieve the logged-in username from the activity's Intent extras.
        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }

        //Creates a welcome text with the user's name.
        TextView usernameTextView = view.findViewById(R.id.textView_homeMyMoodFrag_welcomeUser);
        usernameTextView.setText(username != null ? "Welcome, " + username + "!" : "Welcome!");

        //Set up FloatingActionButton to allow the user to add a new mood event.
        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_homeMyMoodsFrag_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        //Set up profile image click listener to navigate to the personal profile details activity.
        ImageView profileImage = view.findViewById(R.id.ImageView_homeMyMoodsFrag_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        //Initialize AutoCompleteTextView with options for filtering by emotional state.
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        autoCompleteTextView.setAdapter(adapter);
        //Update the emotional state filter when the user selects an option.
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

        //Initialize SearchView to filter mood events by trigger text.
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //When the query is submitted, update the trigger filter and reload the moods.
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTrigger = query;
                loadMoodsFromFirebase();
                return false;
            }
            //Update the trigger filter as text changes and reload the moods.
            @Override
            public boolean onQueryTextChange(String newText) {
                filterTrigger = newText;
                loadMoodsFromFirebase();
                return false;
            }
        });

        //Set up the RecyclerView to display mood events.
        userRecyclerView = view.findViewById(R.id.recyclerview_fragmenthomemymoods_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());//Using a LinearLayoutManager for vertical scrolling.
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);//Initialize the adapter with the mood list and bind it to the RecyclerView.
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        //Set up a click listener for RecyclerView items to navigate to mood event details.
        userRecyclerViewAdapter.setOnItemClickListener(mood -> {
            Intent intent = new Intent(getActivity(), MoodEventDetailsActivity.class);
            intent.putExtra("moodId", mood.getDocId());
            startActivity(intent);
        });

        //Initialize Firestore and obtain a reference to the "moods" collection.
        db = FirebaseFirestore.getInstance();
        moodListRef = db.collection("moods");

        //Load mood events from Firestore using current filters.
        loadMoodsFromFirebase();

        //Set up a CheckBox to enable filtering of moods from the recent week.
        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_recentWeek);
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentweek = isChecked;
            loadMoodsFromFirebase();
        });

        return view;
    }

    /**
     * Called when the fragment resumes.
     * Refreshes the list of mood events to ensure the latest data is displayed.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadMoodsFromFirebase();
    }

    /**
     * Loads mood events from Firestore based on the applied filters.
     *
     * Filters include:
     * - Username (only moods belonging to the logged-in user).
     * - Recent week (if enabled).
     * - Emotional state (if not set to "All Moods").
     * - Trigger text (if provided).
     */
    private void loadMoodsFromFirebase() {
        Log.d("UserProfile", "Loading moods for username: '" + username + "'");
        //Build a query for moods by the logged-in username, ordered by date in descending order.
        Query query = moodListRef.whereEqualTo("username", username)
                .orderBy("dateTime", Query.Direction.DESCENDING);

        //If filtering for recent week is enabled, only include moods from the last 7 days.
        if (filterRecentweek) {
            long recentWeekMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            Timestamp recentWeekTimestamp = new Timestamp(new Date(recentWeekMillis));
            query = query.whereGreaterThanOrEqualTo("dateTime", recentWeekTimestamp);
        }

        //If a specific emotional state filter is applied (other than "All Moods"), add it to the query.
        if (filterEmotionalstate != null && !filterEmotionalstate.equals("All Moods") && !filterEmotionalstate.isEmpty()) {
            String filterValue = filterEmotionalstate.toUpperCase();
            query = query.whereEqualTo("moodState", filterValue);
        }
        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firestore", "Error fetching moods", task.getException());
                return;
            }

            List<Mood> moods = new ArrayList<>();
            //Iterate over the query results.
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                Log.d("Firestore", "Document ID: " + snapshot.getId() + ", Data: " + snapshot.getData());
                //Convert the document snapshot to a Mood object.
                Mood mood = snapshot.toObject(Mood.class);
                //Set the document ID for later reference.
                mood.setDocId(snapshot.getId());
                if (mood.getMoodState() == null) {//If the mood state is missing, default it to HAPPINESS.
                    mood.setMoodState(Mood.MoodState.HAPPINESS);
                }
                moods.add(mood);
            }

            //If a trigger filter is provided, filter the mood list accordingly.
            if (filterTrigger != null && !filterTrigger.isEmpty()) {
                List<Mood> filteredList = new ArrayList<>();
                for (Mood mood : moods) {
                    if (mood.getTrigger() != null && mood.getTrigger().toLowerCase().contains(filterTrigger.toLowerCase())) {
                        filteredList.add(mood);
                    }
                }
                moods = filteredList;
            }

            //Clear the current mood list and add the updated list.
            userMoodList.clear();
            userMoodList.addAll(moods);
            // Notify the adapter so that the RecyclerView updates its display.
            userRecyclerViewAdapter.notifyDataSetChanged();
            Log.d("Firestore", "Total Moods Fetched: " + userMoodList.size());
        });
    }
}

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
 * Fragment that displays the logged-in user's mood history.
 *
 * It also allows users to:
 * View their moods in a RecyclerView.
 * Filter moods by emotional state, time period, or trigger words.
 * Add new mood events.
 * Access their profile details.
 */
public class HomeMyMoodsFrag extends Fragment {

    //Stores the username of the logged-in user.
    private String username;
    //List to store user moods retrieved from Firestore.
    private List<Mood> userMoodList = new ArrayList<>();
    //RecyclerView components for displaying moods.
    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    //Firebase Firestore instance and reference to moods collection.
    private FirebaseFirestore db;
    private CollectionReference moodListRef;
    //Filters for displaying moods.
    private boolean filterRecentweek = false;
    private String filterEmotionalstate = "All Moods";
    private String filterTrigger = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        //Retrieve the username from the Intent.
        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }


        TextView usernameTextView = view.findViewById(R.id.textView_FragmentHomeMyMoods_welcomeUser);
        usernameTextView.setText(username != null ? "Welcome, " + username + "!" : "Welcome!");

        //Floating action button to add a new mood event.
        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_FragmentHomeMyMoods_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        //ImageView that navigates to the user's profile details.
        ImageView profileImage = view.findViewById(R.id.ImageView_FragmentHomeMyMoods_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        //Dropdown menu to filter moods by emotional state.
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

        //SearchView to filter moods by trigger words.
        SearchView searchView = view.findViewById(R.id.searchView_FragmentHomeMyMoods);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTrigger = query;
                loadMoodsFromFirebase();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterTrigger = newText;
                loadMoodsFromFirebase();
                return false;
            }
        });

        //Initialize and set up the RecyclerView.
        userRecyclerView = view.findViewById(R.id.recyclerview_FragmentHomeMyMoods_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        //Handle mood item clicks to view details.
        userRecyclerViewAdapter.setOnItemClickListener(mood -> {
            Intent intent = new Intent(getActivity(), MoodEventDetailsActivity.class);
            intent.putExtra("moodId", mood.getDocId());
            startActivity(intent);
        });

        //Initialize Firestore and reference the moods collection.
        db = FirebaseFirestore.getInstance();
        moodListRef = db.collection("moods");

        //Load moods from Firestore.
        loadMoodsFromFirebase();

        //Checkbox to filter moods from the recent week.
        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_FragmentHomeMyMoods_recentWeek);
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
     * Retrieves and filters the user's moods from Firestore based on applied filters.
     */
    private void loadMoodsFromFirebase() {
        Log.d("UserProfile", "Loading moods for username: '" + username + "'");
        Query query = moodListRef.whereEqualTo("username", username)
                .orderBy("dateTime", Query.Direction.DESCENDING);

        //Filter moods from the last seven days if the checkbox is checked.
        if (filterRecentweek) {
            long recentWeekMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            Timestamp recentWeekTimestamp = new Timestamp(new Date(recentWeekMillis));
            query = query.whereGreaterThanOrEqualTo("dateTime", recentWeekTimestamp);
        }

        //Apply emotional state filter if selected.
        if (filterEmotionalstate != null && !filterEmotionalstate.equals("All Moods") && !filterEmotionalstate.isEmpty()) {
            query = query.whereEqualTo("moodState", filterEmotionalstate.toUpperCase());
        }

        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firestore", "Error fetching moods", task.getException());
                return;
            }

            List<Mood> moods = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                Mood mood = snapshot.toObject(Mood.class);
                mood.setDocId(snapshot.getId());
                moods.add(mood);
            }

            userMoodList.clear();
            userMoodList.addAll(moods);
            userRecyclerViewAdapter.notifyDataSetChanged();
            Log.d("Firestore", "Total Moods Fetched: " + userMoodList.size());
        });
    }
}

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

public class HomeMyMoodsFrag extends Fragment {

    private String username;
    private List<Mood> userMoodList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    private FirebaseFirestore db;
    private CollectionReference moodListRef;
    private boolean filterRecentweek = false;
    private String filterEmotionalstate = "All Moods";
    private String filterTrigger = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }

        TextView usernameTextView = view.findViewById(R.id.textView_FragmentHomeMyMoods_welcomeUser);
        usernameTextView.setText(username != null ? "Welcome, " + username + "!" : "Welcome!");

        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_FragmentHomeMyMoods_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        ImageView profileImage = view.findViewById(R.id.ImageView_FragmentHomeMyMoods_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

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

        userRecyclerView = view.findViewById(R.id.recyclerview_FragmentHomeMyMoods_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        userRecyclerViewAdapter.setOnItemClickListener(mood -> {
            Intent intent = new Intent(getActivity(), MoodEventDetailsActivity.class);
            intent.putExtra("moodId", mood.getDocId());
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        moodListRef = db.collection("moods");

        loadMoodsFromFirebase();

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

    private void loadMoodsFromFirebase() {
        Log.d("UserProfile", "Loading moods for username: '" + username + "'");
        Query query = moodListRef.whereEqualTo("username", username)
                .orderBy("dateTime", Query.Direction.DESCENDING);

        if (filterRecentweek) {
            long recentWeekMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            Timestamp recentWeekTimestamp = new Timestamp(new Date(recentWeekMillis));
            query = query.whereGreaterThanOrEqualTo("dateTime", recentWeekTimestamp);
        }

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
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                Log.d("Firestore", "Document ID: " + snapshot.getId() + ", Data: " + snapshot.getData());
                Mood mood = snapshot.toObject(Mood.class);
                mood.setDocId(snapshot.getId());
                if (mood.getMoodState() == null) {
                    mood.setMoodState(Mood.MoodState.HAPPINESS);
                }
                moods.add(mood);
            }

            if (filterTrigger != null && !filterTrigger.isEmpty()) {
                List<Mood> filteredList = new ArrayList<>();
                for (Mood mood : moods) {
                    if (mood.getTrigger() != null && mood.getTrigger().toLowerCase().contains(filterTrigger.toLowerCase())) {
                        filteredList.add(mood);
                    }
                }
                moods = filteredList;
            }

            userMoodList.clear();
            userMoodList.addAll(moods);
            userRecyclerViewAdapter.notifyDataSetChanged();
            Log.d("Firestore", "Total Moods Fetched: " + userMoodList.size());
        });
    }
}

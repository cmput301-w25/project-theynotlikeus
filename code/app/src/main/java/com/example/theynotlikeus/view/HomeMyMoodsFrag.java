package com.example.theynotlikeus.view;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.UserRecyclerViewAdapter;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A fragment that displays the logged-in user's mood events.
 *
 * This fragment loads mood events from Firebase Firestore and allows the user to filter them.
 */
public class HomeMyMoodsFrag extends Fragment {

    private String username;
    private List<Mood> userMoodList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private RecyclerView.LayoutManager userRecyclerViewLayoutManager;
    private boolean filterRecentweek = false;
    private String filterEmotionalstate = "All Moods";
    private String filterTrigger = "";
    private MoodController moodController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_my_moods, container, false);

        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
        }
        if (username == null || username.isEmpty()) {
            Log.e("HomeMyMoodsFrag", "Username not provided. Using default username.");
            Toast.makeText(getContext(), "Username not provided. Using default user.", Toast.LENGTH_SHORT).show();
            username = "defaultUser";
        }

        TextView usernameTextView = view.findViewById(R.id.textView_HomeMyMoodsFragment_welcomeUser);
        usernameTextView.setText("Welcome, " + username + "!");

        FloatingActionButton addMoodButton = view.findViewById(R.id.floatingActionButton_HomeMyMoodsFragment_addmood);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoodEventActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        ImageView profileImage = view.findViewById(R.id.ImageView_HomeMyMoodsFragment_userProfile);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalProfileDetailsActivity.class);
            startActivity(intent);
        });

        MaterialAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        String[] filterOptions = {"All Moods", "Happiness", "Sadness", "Anger", "Surprise", "Fear", "Disgust", "Shame", "Confusion"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            filterEmotionalstate = parent.getItemAtPosition(position).toString();
            loadMoodsFromFirebase();
        });

        TextInputEditText searchEditText = view.findViewById(R.id.search_edit_text);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterTrigger = searchEditText.getText().toString().trim();
                loadMoodsFromFirebase();
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTrigger = s.toString().trim();
                loadMoodsFromFirebase();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        userRecyclerView = view.findViewById(R.id.recyclerview_HomeMyMoodsFragment_userrecyclerview);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userMoodList);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        userRecyclerViewAdapter.setOnItemClickListener(mood -> {
            Intent intent = new Intent(getActivity(), MoodEventDetailsActivity.class);
            intent.putExtra("mood", mood);
            startActivity(intent);
        });

        moodController = new MoodController();
        loadMoodsFromFirebase();

        CheckBox recentWeekCheckBox = view.findViewById(R.id.checkBox_HomeMyMoodsFragment_recentWeek);
        recentWeekCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRecentweek = isChecked;
            loadMoodsFromFirebase();
        });

        return view;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

                    for (Mood mood : moods) {
                        boolean includeMood = true;

                        if (filterRecentweek) {
                            long oneWeekAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
                            Date moodDate = mood.getDateTime();
                            if (moodDate == null || moodDate.getTime() < oneWeekAgoMillis) {
                                includeMood = false;
                            }
                        }

                        if (filterEmotionalstate != null &&
                                !filterEmotionalstate.equals("All Moods") &&
                                !filterEmotionalstate.isEmpty()) {
                            String moodState = mood.getMoodState().name();
                            if (!moodState.equalsIgnoreCase(filterEmotionalstate)) {
                                includeMood = false;
                            }
                        }

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

                    Collections.sort(filteredMoods, (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));

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
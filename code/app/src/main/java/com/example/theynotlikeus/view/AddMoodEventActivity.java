package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.controller.MoodController;

import java.util.function.Consumer;

/**
 * This activity allows the user to select a mood, specify a trigger, and choose a social situation which is then
 * saved to Firestore via MoodController.
 *
 * Also allows the user to exit the activity using the back button.
 */
public class AddMoodEventActivity extends AppCompatActivity {

    private String username; // Stores the username passed from the previous screen
    private final int trigger_length_limit = 20;
    private MoodController moodController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username"); // Retrieve the username passed from the previous activity.

        // Initialize the MoodController.
        moodController = new MoodController();

        // UI components
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);

        // Setting up the mood spinner with available moods.
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.moods,
                R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Setting up the social situation spinner.
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_situations,
                R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Save button implementation.
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        saveButton.setOnClickListener(v -> {
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
                return;
            }
            Mood mood = new Mood(moodState);

            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                try {
                    validateTrigger(trigger, trigger_length_limit);
                    mood.setTrigger(trigger);
                } catch (ArithmeticException ex) {
                    Toast.makeText(AddMoodEventActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_")
                );
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
                // Optionally handle invalid social situation.
            }

            // Set the username for the mood event.
            mood.setUsername(username);

            // Use MoodController to add the mood.
            moodController.addMood(mood, new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }, new Consumer<Exception>() {
                @Override
                public void accept(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Back button to exit the activity.
        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
    }

    // Helper method to validate trigger length.
    static void validateTrigger(String trigger, int limit) {
        if (trigger.length() > limit) {
            throw new ArithmeticException("Trigger has too many characters!");
        }
    }

    // Helper method to parse mood from a string.
    static Mood.MoodState parseMood(String moodStr) {
        try {
            return Mood.MoodState.valueOf(moodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mood selection.");
        }
    }
}

package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;

import java.io.Serializable;

/**
 * This activity allows the user to edit and delete a mood event by providing a user interface
 * for modifying stored mood data including interactive buttons such as save, delete, and back.
 * It loads an existing mood event from Firestore using a mood ID.
 */
public class EditDeleteMoodActivity extends AppCompatActivity {

    private MoodController moodController; // Use the MoodController for CRUD operations.
    private String moodId; // Stores the ID of the mood document to edit/delete.
    private Mood moodToEdit; // Mood object to be edited.
    private final int trigger_length_limit = 20;

    // UI elements.
    Spinner moodSpinner;
    EditText triggerEditText;
    Spinner socialSituationSpinner;
    Switch geolocationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_mood);

        // Bind UI elements.
        moodSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);
        triggerEditText = findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);
        socialSituationSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_socialsituation);
        geolocationSwitch = findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);
        ImageButton deleteButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        ImageButton backButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        Button saveButton = findViewById(R.id.button_DeleteEditMoodActivity_save);

        // Initialize the MoodController.
        moodController = new MoodController();

        // First, try to retrieve a full Mood object from the Intent extras.
        Mood passedMood = (Mood) getIntent().getSerializableExtra("mood");
        if (passedMood != null) {
            moodToEdit = passedMood;
            moodId = moodToEdit.getDocId();
        } else {
            // Otherwise, retrieve the moodId.
            moodId = getIntent().getStringExtra("moodId");
        }

        // Setup mood spinner using options from resources.
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Setup social situation spinner using options from resources.
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // If we don't already have a Mood object, load it from Firestore.
        if (moodToEdit == null && moodId != null) {
            loadMoodData();
        } else if (moodToEdit != null) {
            // Otherwise, update the UI immediately with the passed Mood.
            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
            triggerEditText.setText(moodToEdit.getTrigger());
            if (moodToEdit.getSocialSituation() != null) {
                socialSituationSpinner.setSelection(getSocialSituationIndex(moodToEdit.getSocialSituation()));
            }
        }

        // Save button: Update the mood document using MoodController.
        saveButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection", Toast.LENGTH_SHORT).show();
                return;
            }
            moodToEdit.setMoodState(moodState);

            // Update the trigger.
            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                if (trigger.length() > trigger_length_limit) {
                    Toast.makeText(EditDeleteMoodActivity.this,
                            "Trigger has too many characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                moodToEdit.setTrigger(trigger);
            }

            // Update the social situation.
            String socialText = socialSituationSpinner.getSelectedItem().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation =
                            Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Invalid social situation", Toast.LENGTH_SHORT).show();
                }
            }

            // Use MoodController to update the mood.
            moodController.updateMood(moodToEdit, () -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Mood updated successfully!", Toast.LENGTH_SHORT).show();
                // Pass the updated mood back so the local copy can be refreshed.
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedMood", (Serializable) moodToEdit);
                setResult(RESULT_OK, resultIntent);
                finish();
            }, e -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Error updating mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        // Delete button: Remove the mood document via MoodController.
        deleteButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            moodController.deleteMood(moodToEdit.getDocId(), () -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Mood deleted successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditDeleteMoodActivity.this, MainActivity.class);
                intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, e -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        // Back button: Simply finish the activity.
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the mood data in case of external updates.
        if (moodToEdit == null && moodId != null) {
            loadMoodData();
        }
    }

    /**
     * Loads the mood data from Firestore using MoodController and updates the UI.
     */
    private void loadMoodData() {
        moodController.getMood(moodId, mood -> {
            moodToEdit = mood;
            if (moodToEdit != null) {
                moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
                triggerEditText.setText(moodToEdit.getTrigger());
                if (moodToEdit.getSocialSituation() != null) {
                    socialSituationSpinner.setSelection(getSocialSituationIndex(moodToEdit.getSocialSituation()));
                }
            }
        }, e -> {
            Toast.makeText(EditDeleteMoodActivity.this,
                    "Error loading mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Returns the index in the social situation spinner that corresponds to the given social situation.
     *
     * @param socialSituation the social situation to match.
     * @return the index of the matching social situation, or 0 if not found.
     */
    private int getSocialSituationIndex(Mood.SocialSituation socialSituation) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) socialSituationSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(
                    socialSituation.toString().replace("_", " "))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the index in the mood spinner that corresponds to the given mood state.
     *
     * @param moodState the mood state to match.
     * @return the index of the matching mood state, or 0 if not found.
     */
    private int getMoodStateIndex(Mood.MoodState moodState) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) moodSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(moodState.toString())) {
                return i;
            }
        }
        return 0;
    }
}

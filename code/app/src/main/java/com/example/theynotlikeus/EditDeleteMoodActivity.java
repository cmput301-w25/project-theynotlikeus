package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditDeleteMoodActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    // This is the Firestore document ID passed from the previous screen
    private String moodId;
    private Mood moodToEdit;

    Spinner moodSpinner;
    EditText triggerEditText;
    EditText socialSituationEditText;
    Switch geolocationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_mood);

        moodSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);
        triggerEditText = findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);
        socialSituationEditText = findViewById(R.id.editText_DeleteEditMoodActivity_socialSituationinput);
        geolocationSwitch = findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);

        ImageButton deleteButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        ImageButton backButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        Button saveButton = findViewById(R.id.button_DeleteEditMoodActivity_save);

        // Initialize Firestore and get the doc ID from the Intent
        db = FirebaseFirestore.getInstance();
        moodId = getIntent().getStringExtra("moodId");

        // Set up spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.addmoodevent_spinner
        );
        adapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        moodSpinner.setAdapter(adapter);

        // Load existing mood data from Firestore
        loadMoodData();

        // Handle SAVE
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

            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                moodToEdit.setTrigger(trigger);
            }

            String socialText = socialSituationEditText.getText().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation =
                            Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Edit social situation", Toast.LENGTH_SHORT).show();
                }
            }

            // If geolocationSwitch is checked => handle Part 4, etc.

            // Use moodToEdit.getDocId() for the reference,
            // which should match the Firestore document ID
            db.collection("moods").document(moodToEdit.getDocId())
                    .set(moodToEdit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Mood updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Error updating mood: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        // Handle DELETE
        deleteButton.setOnClickListener(v -> {
            Toast.makeText(EditDeleteMoodActivity.this, "Delete button clicked", Toast.LENGTH_SHORT).show();
            Log.d("EditDeleteMoodActivity", "Delete button clicked");
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("EditDeleteMoodActivity", "Attempting to delete mood with docId: " + moodToEdit.getDocId());
            db.collection("moods").document(moodToEdit.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Mood deleted successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                        // After deletion, go back to MoodEventDetailsActivity
                        Intent intent = new Intent(EditDeleteMoodActivity.this, MoodEventDetailsActivity.class);
                        intent.putExtra("moodId", moodId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Error deleting mood: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                        Log.e("EditDeleteMoodActivity", "Delete failed", e);
                    });
        });


        /* Handle DELETE
        deleteButton.setOnClickListener(v -> {
            Toast.makeText(EditDeleteMoodActivity.this, "Delete button clicked", Toast.LENGTH_SHORT).show();
            Log.d("EditDeleteMoodActivity", "Delete button clicked");
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("EditDeleteMoodActivity", "Attempting to delete mood with docId: " + moodToEdit.getDocId());
            db.collection("moods").document(moodToEdit.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Mood deleted successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                        // After deletion, navigate back to MainActivity with homeMyMoodsFrag loaded
                        Intent intent = new Intent(EditDeleteMoodActivity.this, MainActivity.class);
                        intent.putExtra("fragmentToLoad", "homeMyMoodsFrag");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Error deleting mood: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                        Log.e("EditDeleteMoodActivity", "Delete failed", e);
                    });
        });*/
        backButton.setOnClickListener(v -> {
            Toast.makeText(EditDeleteMoodActivity.this, "Back button clicked", Toast.LENGTH_SHORT).show();
            Log.d("EditDeleteMoodActivity", "Back button clicked");
            Intent intent = new Intent(EditDeleteMoodActivity.this, MoodEventDetailsActivity.class);
            intent.putExtra("moodId", moodId);
            startActivity(intent);
            finish();
        });

    }

    private void loadMoodData() {
        db.collection("moods").document(moodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert to Mood object
                        moodToEdit = documentSnapshot.toObject(Mood.class);

                        // Set the docId on the object
                        if (moodToEdit != null) {
                            moodToEdit.setDocId(documentSnapshot.getId());

                            // Now populate the UI
                            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
                            triggerEditText.setText(moodToEdit.getTrigger());
                            if (moodToEdit.getSocialSituation() != null) {
                                socialSituationEditText.setText(
                                        moodToEdit.getSocialSituation().toString().replace("_", " ")
                                );
                            }
                            // Geolocation part (Part 4) if needed
                        }
                    } else {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Mood not found in Firestore.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            EditDeleteMoodActivity.this,
                            "Error loading mood: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // Used by the spinner to identify which mood is selected
    private int getMoodStateIndex(Mood.MoodState moodState) {
        ArrayAdapter<CharSequence> adapter =
                (ArrayAdapter<CharSequence>) moodSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(moodState.toString())) {
                return i;
            }
        }
        return 0;
    }
}

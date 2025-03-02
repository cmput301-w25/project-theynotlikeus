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
    private String moodId;
    private Mood moodToEdit;

    Spinner moodSpinner;
    EditText triggerEditText;
    Spinner socialSituationSpinner;
    Switch geolocationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_mood);

        moodSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);
        triggerEditText = findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);
        socialSituationSpinner = findViewById(R.id.spinner_activityeditdeletemoodevent_socialsituation);
        geolocationSwitch = findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);

        ImageButton deleteButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        ImageButton backButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        Button saveButton = findViewById(R.id.button_DeleteEditMoodActivity_save);

        db = FirebaseFirestore.getInstance();
        moodId = getIntent().getStringExtra("moodId");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.addmoodevent_spinner
        );
        adapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        moodSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.addmoodevent_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        loadMoodData();

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

            String socialText = socialSituationSpinner.getSelectedItem().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation =
                            Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Edit social situation", Toast.LENGTH_SHORT).show();
                }
            }

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

        deleteButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("moods").document(moodToEdit.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Mood deleted successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
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
                    });
        });

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoodData();
    }

    private void loadMoodData() {
        db.collection("moods").document(moodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        moodToEdit = documentSnapshot.toObject(Mood.class);
                        if (moodToEdit != null) {
                            moodToEdit.setDocId(documentSnapshot.getId());
                            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
                            triggerEditText.setText(moodToEdit.getTrigger());
                            if (moodToEdit.getSocialSituation() != null) {
                                socialSituationSpinner.setSelection(
                                        getSocialSituationIndex(moodToEdit.getSocialSituation())
                                );
                            }
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

    private int getSocialSituationIndex(Mood.SocialSituation socialSituation) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) socialSituationSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(socialSituation.toString().replace("_", " "))) {
                return i;
            }
        }
        return 0;
    }

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

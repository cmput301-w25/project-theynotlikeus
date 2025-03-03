package com.example.theynotlikeus;

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

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This fragment allows the user to edit and delete a mood event by providing a user interface for modifying stored mood data
 * including interactive buttons like save, delete and back (to navigate back to MainActivity).
 *
 * It loads an existing mood event from Firestore using a mood ID.
 */
public class EditDeleteMoodActivity extends AppCompatActivity {

    private FirebaseFirestore db;//Firestore database instance.
    private String moodId; //Stores the ID of the mood document to edit/delete.
    private Mood moodToEdit; //Mood object to be edited.

    //Defining variables that will store the ids of UI elements
    Spinner moodSpinner;
    EditText triggerEditText;
    Spinner socialSituationSpinner;
    Switch geolocationSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_mood);

        //Bind UI elements to their respective views in order to implement them
        moodSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);
        triggerEditText = findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);
        socialSituationSpinner = findViewById(R.id.spinner_activityeditdeletemoodevent_socialsituation);
        geolocationSwitch = findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);
        ImageButton deleteButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        ImageButton backButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        Button saveButton = findViewById(R.id.button_DeleteEditMoodActivity_save);


        db = FirebaseFirestore.getInstance(); //Initialize Firestore

        moodId = getIntent().getStringExtra("moodId"); //Retrieving the moodId from the Intent extras.

        //load existing mood data from Firestore.
        loadMoodData();

        //Setting up the mood spinner with mood options from resources.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner
        );
        adapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(adapter);


        //Setting up the social situation spinner with options from resources.
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);


        //Implementing the save button
        saveButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show(); //Error handling
                return;
            }
            String selectedMood = moodSpinner.getSelectedItem().toString(); //Getting the selected mood state from the spinner.
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase()); //Converting the selected string to MoodState enum.
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection", Toast.LENGTH_SHORT).show(); //Error handling
                return;
            }

            //Updating the mood object's state.
            moodToEdit.setMoodState(moodState);

            //Updating all the fields

            String trigger = triggerEditText.getText().toString().trim(); //Update the trigger
            if (!trigger.isEmpty()) {
                moodToEdit.setTrigger(trigger);
            }

            String socialText = socialSituationSpinner.getSelectedItem().toString().trim(); //Update the social situation
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation =
                            Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Edit social situation", Toast.LENGTH_SHORT).show(); //Error handling
                }
            }

            //Save the updated mood object back to Firestore.
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
                    .addOnFailureListener(e -> { //Error handling
                        Toast.makeText(
                                EditDeleteMoodActivity.this,
                                "Error updating mood: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        //Implementing a delete button listener to remove the mood document from Firestore and then naviagating to the landing page
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

                        //Navigate back to MainActivity, loading the homeMyMoodsFrag fragment.
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

        //Implementing the back button to take the user to the Mood details activity.
        backButton.setOnClickListener(v -> finish());
    }

    /**
     *Reload the mood data each time the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadMoodData();
    }

    /**
     *Loads the mood data from Firestore and updates the UI elements.
     */
    private void loadMoodData() {
        db.collection("moods").document(moodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        moodToEdit = documentSnapshot.toObject(Mood.class);//Convert the Firestore document to a Mood object.
                        if (moodToEdit != null) {
                            moodToEdit.setDocId(documentSnapshot.getId()); //Set the document ID in the Mood object for reference.
                            //Populating the fields with the data retrieved from the database
                            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState())); //Mood state
                            triggerEditText.setText(moodToEdit.getTrigger()); //The current trigger
                            if (moodToEdit.getSocialSituation() != null) { //Set the social situation spinner selection if available.
                                socialSituationSpinner.setSelection(
                                        getSocialSituationIndex(moodToEdit.getSocialSituation())
                                );
                            }
                        }
                    } else { //If the mood ID does not exist in the database, the error is handled by printing an error message.
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

    /**
     * Returns the index in the social situation spinner that corresponds to the given social situation.
     *
     * @param socialSituation the social situation to match.
     * @return the index of the matching social situation, or 0 if not found.
     */
    private int getSocialSituationIndex(Mood.SocialSituation socialSituation) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) socialSituationSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            // Compare ignoring case and replacing underscores with spaces.
            if (adapter.getItem(i).toString().equalsIgnoreCase(socialSituation.toString().replace("_", " "))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the index in the mood spinner that corresponds to the given mood state.
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

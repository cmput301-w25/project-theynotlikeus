package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity allows the user to select a mood, specify a trigger, and choose a social situation which is then
 * saved to Firestore.
 *
 * Also allows the user to exit the activity using the back button
 */
public class AddMoodEventActivity extends AppCompatActivity {


    private String username; //Stores the username passed from the previous screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);


        username = getIntent().getStringExtra("username"); //Retrieve the username passed from the previous activity.

        //UI components to their views.
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);

        //Setting up a mood spinner that contains a list of all possible moods the user can select
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.moods,
                R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        //Setting up the social situation spinner
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_situations,
                R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        //Implementing the save button to save all the mood details into Firebase
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);

        saveButton.setOnClickListener(v -> {
            String selectedMood = moodSpinner.getSelectedItem().toString(); //String vaue of the mood selected
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase()); //Convert the selected string to a MoodState enum.
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show(); // Error handling : notify the user if an invalid mood is selected.
                return;
            }
            //Create a new Mood object with the selected mood state.
            Mood mood = new Mood(moodState);

            String trigger = triggerEditText.getText().toString().trim();//Get the trigger text, if provided, and set it in the mood object.
            if (!trigger.isEmpty()) {
                mood.setTrigger(trigger);
            }


            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();//Get the selected social situation from the spinner.
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_") //Convert the social situation string to a SocialSituation enum.
                );
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
                // If the conversion fails, no action is taken.
            }

            //Recording the username of the user adding the mood event
            mood.setUsername(username);

            //Getting a Firestore instance and adding the new mood document to the "moods" collection.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("moods")
                    .add(mood)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show(); //Error handling : notify the user if there is an error saving the mood.
                    });
        });

        //Implementing the back button to finish the back button when clicked
        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
    }
}

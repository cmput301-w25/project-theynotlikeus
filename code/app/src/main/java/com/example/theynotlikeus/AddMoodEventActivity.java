package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
/**
 * * Activity for users to add a new mood event.
 * Allows users to select a mood state, enter a trigger, and optionally add a social situation.
 *
 *
 */
public class AddMoodEventActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        // Retrieve the username passed from the previous screen

        username = getIntent().getStringExtra("username");

        // UI references
        Spinner moodSpinner = findViewById(R.id.spinner_activitymoodevent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_activitymoodevent_trigger);
        EditText socialSituationEditText = findViewById(R.id.edittext_activitymoodevent_socialsituation);
        Switch geolocationSwitch = findViewById(R.id.switch_activitymoodevent_geolocation); // optional: for geolocation logic

        // Setup spinner for mood selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.moods,
                R.layout.addmoodevent_spinner
        );
        adapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        moodSpinner.setAdapter(adapter);
        // End of spinner adjustments

        Button saveButton = findViewById(R.id.button_activitymoodevent_save);


        saveButton.setOnClickListener(v -> {
            // Get the selected mood from the spinner
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                // Convert selected string to MoodState enum
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new Mood object
            Mood mood = new Mood(moodState);

            // Set optional trigger
            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                mood.setTrigger(trigger);
            }

            // Set optional social situation
            String socialText = socialSituationEditText.getText().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    // Convert the text to a SocialSituation enum
                    // Replace spaces with underscores and convert to upper case
                    Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    mood.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    // You can optionally notify the user if the provided social situation is invalid
                }
            }

            // Optionally, handle geolocation if the switch is on
            // For example, you might retrieve the device's current location here and set it:
            // if (geolocationSwitch.isChecked()) { mood.setLocation(latitude, longitude); }

            // Set the username from the Intent extra
            mood.setUsername(username);

            // Save the Mood object to Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("moods")
                    .add(mood)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity once saved
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Back button to return to the previous screen
        findViewById(R.id.button_activitymoodevent_backbutton).setOnClickListener(v -> finish());
    }
}

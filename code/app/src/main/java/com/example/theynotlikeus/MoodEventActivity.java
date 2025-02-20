package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MoodEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event);

        Button saveButton = findViewById(R.id.button_activitymoodevent_save);
        saveButton.setOnClickListener(v -> {

            Spinner moodSpinner = findViewById(R.id.spinner_activitymoodevent_currentmood);
            EditText triggerEditText = findViewById(R.id.edittext_activitymoodevent_trigger);
            EditText socialSituationEditText = findViewById(R.id.edittext_activitymoodevent_socialsituation);
            Switch geoSwitch = findViewById(R.id.switch_activitymoodevent_geolocation);


            String moodString = moodSpinner.getSelectedItem().toString().toUpperCase();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(moodString);
            } catch (IllegalArgumentException e) {
                Toast.makeText(MoodEventActivity.this, "Invalid mood selected.", Toast.LENGTH_SHORT).show();
                return;
            }
            Mood mood = new Mood(moodState);


            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                mood.setTrigger(trigger);
            }


            String socialSituationString = socialSituationEditText.getText().toString().trim().toUpperCase();
            if (!socialSituationString.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(socialSituationString);
                    mood.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MoodEventActivity.this, "Invalid social situation.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            if (geoSwitch.isChecked()) {
                mood.setLocation(0.0, 0.0);
            }


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("moods")
                    .add(mood)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(MoodEventActivity.this, "Mood saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        findViewById(R.id.button_activitymoodevent_backbutton).setOnClickListener(v -> finish());
    }
}

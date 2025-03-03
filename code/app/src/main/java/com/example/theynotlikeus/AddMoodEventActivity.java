package com.example.theynotlikeus;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.theynotlikeus.Mood;
import com.example.theynotlikeus.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMoodEventActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");

        Spinner moodSpinner = findViewById(R.id.spinner_activitymoodevent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_activitymoodevent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_activitymoodevent_socialsituation);

        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.moods,
                R.layout.addmoodevent_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        moodSpinner.setAdapter(moodAdapter);

        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_situations,
                R.layout.addmoodevent_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        Button saveButton = findViewById(R.id.button_activitymoodevent_save);
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
                mood.setTrigger(trigger);
            }

            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_")
                );
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
            }

            mood.setUsername(username);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("moods")
                    .add(mood)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        findViewById(R.id.button_activitymoodevent_backbutton).setOnClickListener(v -> finish());

    }
}

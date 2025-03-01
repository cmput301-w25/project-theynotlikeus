package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDeleteMoodActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    //From the last fragment (view mood details)
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


        moodSpinner=findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);

        triggerEditText=findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);

       socialSituationEditText=findViewById(R.id.editText_DeleteEditMoodActivity_socialSituationinput);

        geolocationSwitch=findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);

        ImageButton deleteButton=findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        ImageButton backButton=findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        Button saveButton=findViewById(R.id.button_DeleteEditMoodActivity_save);



        db=FirebaseFirestore.getInstance();
        moodId=getIntent().getStringExtra("moodId"); //moodID from the last fragment


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.moods, R.layout.addmoodevent_spinner);
        adapter.setDropDownViewResource(R.layout.addmoodevent_spinner);
        moodSpinner.setAdapter(adapter);
        loadMoodData();




        //onclicklisteners for all the buttons

        saveButton.setOnClickListener(v -> {
            String selectedMood=moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState=Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection", Toast.LENGTH_SHORT).show();
                return;
            }
            moodToEdit.setMoodState(moodState);
            String trigger=triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                moodToEdit.setTrigger(trigger);
            }
            String socialText=socialSituationEditText.getText().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation=Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Edit social situation", Toast.LENGTH_SHORT).show();
                }
            }
            if (geolocationSwitch.isChecked()) {
                //Part 4
            }



            db.collection("moods").document(moodId)
                    .set(moodToEdit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditDeleteMoodActivity.this, "Mood updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditDeleteMoodActivity.this, "Error updating mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        /* Delete button */
        deleteButton.setOnClickListener(v -> {
            db.collection("moods").document(moodId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditDeleteMoodActivity.this, "Mood deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditDeleteMoodActivity.this, "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


        backButton.setOnClickListener(v -> finish());
    }

    private void loadMoodData() {
        db.collection("moods").document(moodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        moodToEdit=documentSnapshot.toObject(Mood.class);
                        //Loading all the mood data in their respective fields
                        if (moodToEdit != null) {
                            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
                            triggerEditText.setText(moodToEdit.getTrigger());
                            socialSituationEditText.setText(moodToEdit.getSocialSituation().toString().replace("_", " "));
                            //Dont forget geolocation in part 4
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditDeleteMoodActivity.this, "Error loading mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //used in mood spinner to identify the mood selected by the user
    private int getMoodStateIndex(Mood.MoodState moodState) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) moodSpinner.getAdapter();
        for (int i=0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(moodState.toString())) {
                return i;
            }
        }
        return 0;
    }
}

//package com.example.theynotlikeus;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import com.google.android.gms.auth.api.signin.internal.Storage;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.example.theynotlikeus.Mood;
////**
// * This activity allows the user to select a mood, specify a trigger, and choose a social situation which is then
// * saved to Firestore.
// *
// * Also allows the user to exit the activity using the back button
// */
//public class AddMoodEventActivity extends AppCompatActivity {
//
//    private String username; //Stores the username passed from the previous screen
//    private int trigger_length_limit = 20;
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private Uri imageUri;
//    private ImageView moodImageView;
//    private StorageReference storageRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_mood_event);
//
//        username = getIntent().getStringExtra("username"); //Retrieve the username passed from the previous activity.
//
//        //UI components to their views.
//        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
//        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
//        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
//
//        //Setting up a mood spinner that contains a list of all possible moods the user can select
//        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.moods,
//                R.layout.add_mood_event_spinner
//        );
//        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
//        moodSpinner.setAdapter(moodAdapter);
//
//        //Setting up the social situation spinner
//        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.social_situations,
//                R.layout.add_mood_event_spinner
//        );
//        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
//        socialSituationSpinner.setAdapter(socialAdapter);
//
//        //Implementing the save button to save all the mood details into Firebase
//        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
//        saveButton.setOnClickListener(v -> {
//            String selectedMood = moodSpinner.getSelectedItem().toString();
//            Mood.MoodState moodState;
//            try {
//                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
//            } catch (IllegalArgumentException e) {
//                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Mood mood = new Mood(moodState);
//
//            String trigger = triggerEditText.getText().toString().trim();
//            if (!trigger.isEmpty()) {
//                mood.setTrigger(trigger);
//            }
//            try {
//                int trigger_length = trigger.length();
//                if (trigger_length > trigger_length_limit) {
//                    throw new ArithmeticException("Trigger has too many characters!");
//                }
//            } catch (ArithmeticException e) {
//                Toast.makeText(AddMoodEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
//            try {
//                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
//                        selectedSocial.toUpperCase().replace(" ", "_")
//                );
//                mood.setSocialSituation(socialSituation);
//            } catch (IllegalArgumentException e) {
//            }
//
//            //Recording the username of the user adding the mood event
//            mood.setUsername(username);
//
//            //Getting a Firestore instance and adding the new mood document to the "moods" collection.
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("moods")
//                    .add(mood)
//                    .addOnSuccessListener(documentReference -> {
//                        Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
//                        finish();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        });
//
//        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
//
//    }
//
//    // Helper Methods (Static)
//
//    // check trigger length
//    static void validateTrigger(String trigger, int limit) {
//        if (trigger.length() > limit) {
//            throw new ArithmeticException("Trigger has too many characters!");
//        }
//    }
//    // check for invalid mood selected
//    static Mood.MoodState parseMood(String moodStr) {
//        try {
//            return Mood.MoodState.valueOf(moodStr.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid mood selection.");
//        }
//    }
//}

package com.example.theynotlikeus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddMoodEventActivity extends AppCompatActivity {

    private String username;
    private int trigger_length_limit = 200;
    private Uri imageUri;
    private ImageView moodImageView;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");

        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
        moodImageView = findViewById(R.id.imageview_mood_photo);
        Button selectImageButton = findViewById(R.id.button_select_photo);
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        ImageButton backButton = findViewById(R.id.button_ActivityAddMoodEvent_backbutton);
        Switch publicButton = findViewById(R.id.switch_ActivityAddMoodEvent_privacy);

        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner);
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner);
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        selectImageButton.setOnClickListener(v -> selectImage());

        saveButton.setOnClickListener(v -> {
            String selectedMood = moodSpinner.getSelectedItem().toString();
            String trigger = triggerEditText.getText().toString().trim();
            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();

            if (trigger.length() > trigger_length_limit) {
                Toast.makeText(this, "Reason has too many characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            Mood mood = new Mood(Mood.MoodState.valueOf(selectedMood.toUpperCase()));
            mood.setTrigger(trigger);
            mood.setSocialSituation(Mood.SocialSituation.valueOf(selectedSocial.toUpperCase().replace(" ", "_")));
            mood.setUsername(username);
            mood.setPublic(publicButton.isChecked()); // If checked, it's public; otherwise, private

            if (imageUri != null) {
                uploadImageAndSaveMood(mood);
            } else {
                saveMoodToFirestore(mood, null);
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    moodImageView.setImageURI(imageUri);
                }
            });

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageAndSaveMood(Mood mood) {
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveMoodToFirestore(mood, uri.toString()))
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void saveMoodToFirestore(Mood mood, String imageUrl) {
        if (imageUrl != null) {
            mood.setPhotoUrl(imageUrl);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("moods")
                .add(mood)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving mood", Toast.LENGTH_SHORT).show());
    }
}


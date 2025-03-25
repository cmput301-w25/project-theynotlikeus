package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

/**
 * Activity for displaying the details of a mood event.
 *
 * It now receives the full Mood object via Intent and updates the UI directly.
 */
public class MoodEventDetailsActivity extends AppCompatActivity {

    MaterialTextView socialSituationTextView;
    MaterialTextView dateTextView;
    MaterialTextView triggerTextView;
    MaterialTextView usernameTextView;
    MaterialTextView locationTextView;
    ShapeableImageView moodImageView;
    MaterialButton backButton;
    MaterialButton editButton;
    ShapeableImageView uploadedImage;
    MaterialButton commentButton;
    private static final int EDIT_MOOD_REQUEST = 1;

    // Hold the passed Mood object.
    private Mood mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);

        // Updated to use Material Component IDs from the revised layout.
        socialSituationTextView = findViewById(R.id.material_text_socialsituation);
        dateTextView = findViewById(R.id.material_text_dateandtime);
        triggerTextView = findViewById(R.id.material_text_triggervalue);
        usernameTextView = findViewById(R.id.material_text_username);
        locationTextView = findViewById(R.id.material_text_location);
        moodImageView = findViewById(R.id.shapeable_imageview_mood);
        editButton = findViewById(R.id.material_button_edit);
        backButton = findViewById(R.id.material_button_back);
        commentButton = findViewById(R.id.material_button_comment);
        uploadedImage = findViewById(R.id.shapeable_imageview_uploadedphoto);

        // Retrieve the full Mood object from the Intent extras.
        mood = (Mood) getIntent().getSerializableExtra("mood");

        if (mood == null) {
            finish();
            return;
        }

        updateUI();

        /* Code for startActivityForResult to update the edited moods from:
         * https://stackoverflow.com/questions/37768604/how-to-use-startactivityforresult
         * Authored by: Farbod Salamat-Zadeh
         * Taken by: Ercel Angeles on March 15, 2025
         */
        editButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Edit button clicked");
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("mood", mood);
            startActivityForResult(intent, EDIT_MOOD_REQUEST);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodEventDetailsActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        commentButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Comment button clicked");
            Intent intent = new Intent(MoodEventDetailsActivity.this, ViewCommentsActivity.class);
            intent.putExtra("mood", mood);
            startActivity(intent);
        });
    }

    /**
     * Updates the UI elements based on the passed Mood object.
     */
    private void updateUI() {
        socialSituationTextView.setText(mood.getSocialSituation() != null
                ? mood.getSocialSituation().toString() : "Unknown");
        triggerTextView.setText(mood.getTrigger() != null
                ? mood.getTrigger() : "No reason provided");
        usernameTextView.setText(mood.getMoodState() != null
                ? mood.getMoodState().toString() : "Unknown");

        int iconRes = getMoodIcon(mood.getMoodState());
        moodImageView.setImageResource(iconRes);

        dateTextView.setText(mood.getDateTime() != null ? mood.getDateTime().toString() : "Unknown");

        // Update location text: display latitude and longitude if available.
        Double latitude = mood.getLatitude();
        Double longitude = mood.getLongitude();
        if (latitude != null && longitude != null) {
            locationTextView.setText("Location: " + latitude + ", " + longitude);
        } else {
            locationTextView.setText("Location: Unknown");
        }

        // Load the mood image using Glide.
        if (mood.getPhotoUrl() != null && !mood.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(mood.getPhotoUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(uploadedImage);
        } else {
            uploadedImage.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the appropriate icon resource for a given mood state.
     *
     * @param moodState The mood state to get an icon for.
     * @return The resource ID of the corresponding mood icon.
     */
    private int getMoodIcon(Mood.MoodState moodState) {
        if (moodState == null) {
            return R.drawable.ic_happy_emoticon;
        }
        switch (moodState) {
            case ANGER:
                return R.drawable.ic_angry_emoticon;
            case CONFUSION:
                return R.drawable.ic_confused_emoticon;
            case DISGUST:
                return R.drawable.ic_disgust_emoticon;
            case FEAR:
                return R.drawable.ic_fear_emoticon;
            case HAPPINESS:
                return R.drawable.ic_happy_emoticon;
            case SADNESS:
                return R.drawable.ic_sad_emoticon;
            case SHAME:
                return R.drawable.ic_shame_emoticon;
            case SURPRISE:
                return R.drawable.ic_surprised_emoticon;
            default:
                return R.drawable.ic_happy_emoticon;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOOD_REQUEST && resultCode == RESULT_OK && data != null) {
            Mood updatedMood = (Mood) data.getSerializableExtra("mood");
            if (updatedMood != null) {
                mood = updatedMood;
                updateUI();
            }
        }
    }
}

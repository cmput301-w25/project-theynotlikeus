package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;

/**
 * Activity for displaying the details of a mood event.
 *
 * It now receives the full Mood object via Intent and updates the UI directly.
 */
public class MoodEventDetailsActivity extends AppCompatActivity {

    TextView socialSituationTextView;
    TextView dateTextView;
    TextView triggerTextView;
    TextView usernameTextView;
    ImageView moodImageView;
    ImageButton backButton;
    ImageButton editButton;
    Button commentButton;
    private static final int EDIT_MOOD_REQUEST = 1;

    // Hold the passed Mood object.
    private Mood mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);

        socialSituationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityMoodEventDetails_dateandtime);
        triggerTextView = findViewById(R.id.textview_ActivityMoodEventDetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_ActivityMoodEventDetails_username);
        moodImageView = findViewById(R.id.imageview_ActivityMoodEventDetails_moodimage);
        editButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_editbutton);
        backButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_backbutton);
        commentButton = findViewById(R.id.commentButton);

        // Retrieve the full Mood object from the Intent extras.
        mood = (Mood) getIntent().getSerializableExtra("mood");

        if (mood == null) {
            //Toast.makeText(this, "Mood details unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI();
        
        /* Code for startActivityForResult to update the edited moods from: https://stackoverflow.com/questions/37768604/how-to-use-startactivityforresult
         * Authored by: Farbod Salamat-Zadeh
         * Taken by: Ercel Angeles
         * Taken from: March 15, 2025
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
        // Update location TextView

        TextView locationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_location);
        Double latitude = mood.getLatitude();
        Double longitude = mood.getLongitude();

        // show latitude and longitude, display unknown if null value
        if (latitude != null && longitude != null) {
            locationTextView.setText("Location: " + latitude + ", " + longitude);
        } else {
            locationTextView.setText("Location: Unknown");
        }

        // Load the mood image using Glide
        if (mood.getPhotoUrl() != null && !mood.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(mood.getPhotoUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into((ImageView) findViewById(R.id.imageview_ActivityMoodEventDetails_uploadedphoto));
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

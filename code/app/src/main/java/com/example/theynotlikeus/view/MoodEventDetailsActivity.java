package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;

import java.io.Serializable;

/**
 * Activity for displaying the details of a mood event.
 * It receives a full Mood object via Intent and updates the UI directly.
 */
public class MoodEventDetailsActivity extends AppCompatActivity {

    private static final int EDIT_MOOD_REQUEST = 1;

    // UI elements bound to IDs defined in the updated XML layout.
    private ImageButton backButton;
    private ImageButton editButton;
    private TextView titleTextView;
    private TextView usernameTextView;
    private ImageView moodImageView;
    private TextView socialSituationTextView;
    private TextView dateTextView;
    private TextView triggerTextView;
    private TextView locationTextView;
    private Button commentButton;
    private ImageView uploadedImage;

    // Hold the passed Mood object.
    private Mood mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);

        // Bind UI elements using the IDs from the provided XML.
        backButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_backbutton);
        editButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_editbutton);
        titleTextView = findViewById(R.id.textview_ActivityMoodEventDetails_title);
        usernameTextView = findViewById(R.id.textview_ActivityMoodEventDetails_username);
        moodImageView = findViewById(R.id.imageview_ActivityMoodEventDetails_moodimage);
        socialSituationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityMoodEventDetails_dateandtime);
        triggerTextView = findViewById(R.id.textview_ActivityMoodEventDetails_triggervalue);
        locationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_location);
        commentButton = findViewById(R.id.commentButton);
        uploadedImage = findViewById(R.id.imageview_ActivityMoodEventDetails_uploadedphoto);

        // Retrieve the full Mood object from the Intent extras.
        mood = (Mood) getIntent().getSerializableExtra("mood");
        if (mood == null) {
            Toast.makeText(this, "Mood data not available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI();

        // Edit button: launch the edit/delete activity and wait for a result.
        editButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Edit button clicked");
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("mood", mood);
            startActivityForResult(intent, EDIT_MOOD_REQUEST);
        });

        // Back button: navigate back to MainActivity and load the "HomeMyMoodsFrag" fragment.
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodEventDetailsActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Comments button: launch the ViewCommentsActivity.
        commentButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Comment button clicked");
            Intent intent = new Intent(MoodEventDetailsActivity.this, ViewCommentsActivity.class);
            intent.putExtra("mood", mood);
            startActivity(intent);
        });
    }

    /**
     * Updates the UI elements with values from the Mood object.
     */
    private void updateUI() {
        // Update title if needed.
        titleTextView.setText(getString(R.string.your_mood_details));

        // Display the username.
        usernameTextView.setText(mood.getMoodState().toString() != null ? mood.getMoodState().toString() : "Unknown");

        // Display social situation.
        if (mood.getSocialSituation() != null) {
            socialSituationTextView.setText(mood.getSocialSituation().toString());
        } else {
            socialSituationTextView.setText("Unknown");
        }

        // Display the trigger text.
        triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "No reason provided");

        // Display the date/time (assuming mood.getDateTime() returns a Date or String).
        dateTextView.setText(mood.getDateTime() != null ? mood.getDateTime().toString() : "Unknown");

        // Display the location if available.
        if (mood.getLatitude() != null && mood.getLongitude() != null) {
            locationTextView.setText("Location: " + mood.getLatitude() + ", " + mood.getLongitude());
        } else {
            locationTextView.setText("Location: Unknown");
        }

        // Set the mood emoticon image based on the mood state.
        moodImageView.setImageResource(getMoodIcon(mood.getMoodState()));

        // Load the uploaded photo using Glide if a URL is available.
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

    /**
     * Handles the result from the edit/delete activity.
     */
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

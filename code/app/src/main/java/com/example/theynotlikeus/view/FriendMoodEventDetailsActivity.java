package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;

/**
 * Activity for displaying a friend's mood event details.
 * It receives a full Mood object via Intent extras and updates the UI.
 */
public class FriendMoodEventDetailsActivity extends AppCompatActivity {

    private TextView socialSituationTextView;
    private TextView dateTextView;
    private TextView triggerTextView;
    private TextView usernameTextView;
    private TextView moodTypeTextView;
    private TextView locationTextView; // New view to display location details.
    private ImageView moodImageView;
    private ImageButton backButton;

    // Hold the passed Mood object.
    private Mood mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_mood_event_details);

        socialSituationTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_dateandtime);
        triggerTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_username);
        moodTypeTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_moodtype);
        locationTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_location); // Location TextView
        moodImageView = findViewById(R.id.imageview_ActivityFriendMoodEventDetails_moodimage);
        backButton = findViewById(R.id.imagebutton_ActivityFriendMoodEventDetails_backbutton);

        // Retrieve the full Mood object from the Intent extras.
        mood = (Mood) getIntent().getSerializableExtra("mood");
        if (mood == null) {
            Toast.makeText(this, "Mood details unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI();

        // Simply finish the activity when the back button is clicked.
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Updates the UI elements based on the passed Mood object.
     */
    private void updateUI() {
        // Update text fields.
        socialSituationTextView.setText(mood.getSocialSituation() != null
                ? mood.getSocialSituation().toString() : "Unknown");
        triggerTextView.setText(mood.getTrigger() != null
                ? mood.getTrigger() : "No trigger provided");
        usernameTextView.setText(mood.getUsername() != null
                ? mood.getUsername() : "Unknown");
        // Set mood type from mood state.
        moodTypeTextView.setText(mood.getMoodState() != null
                ? mood.getMoodState().name() : "Unknown");

        // Set mood icon.
        int iconRes = getMoodIcon(mood.getMoodState());
        moodImageView.setImageResource(iconRes);

        // Update date and time.
        dateTextView.setText(mood.getDateTime() != null
                ? mood.getDateTime().toString() : "Unknown");

        // Display location details if available.
        if (mood.getLatitude() != null && mood.getLongitude() != null) {
            String locationText = "Location: (" + mood.getLatitude() + ", " + mood.getLongitude() + ")";
            locationTextView.setText(locationText);
        } else {
            locationTextView.setText("Location not available");
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
}

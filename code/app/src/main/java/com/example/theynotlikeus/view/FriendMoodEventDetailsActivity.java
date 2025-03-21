package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.theynotlikeus.singleton.MyApp;
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
    private TextView locationTextView;
    private ImageView moodImageView;
    private ImageButton backButton;
    private Button commentsButton; // Comments button

    // Hold the passed Mood object and the logged-in user's username.
    private Mood mood;
    private String loggedInUsername; // This is the current user's username from the singleton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_mood_event_details);

        socialSituationTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_dateandtime);
        triggerTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_username);
        moodTypeTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_moodtype);
        locationTextView = findViewById(R.id.textview_ActivityFriendMoodEventDetails_location);
        moodImageView = findViewById(R.id.imageview_ActivityFriendMoodEventDetails_moodimage);
        backButton = findViewById(R.id.imagebutton_ActivityFriendMoodEventDetails_backbutton);
        commentsButton = findViewById(R.id.button4);  // Ensure your XML has a Button with id "button4" for Comments

        // Retrieve the full Mood object from the Intent extras.
        mood = (Mood) getIntent().getSerializableExtra("mood");
        // Retrieve the logged-in username from the singleton (MyApp)
        loggedInUsername = ((MyApp) getApplicationContext()).getUsername();

        if (mood == null) {
            Toast.makeText(this, "Mood details unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI();

        // Back button simply finishes the activity.
        backButton.setOnClickListener(v -> finish());

        // Comments button navigates to ViewCommentsActivity.
        // We pass the Mood object and the logged-in user's username.
        commentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendMoodEventDetailsActivity.this, ViewCommentsActivity.class);
            intent.putExtra("mood", mood);
            intent.putExtra("username", loggedInUsername);
            Toast.makeText(FriendMoodEventDetailsActivity.this, "Logged in as: " + loggedInUsername, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
    }

    /**
     * Updates the UI elements based on the passed Mood object.
     */
    private void updateUI() {
        // Display the mood owner's details.
        socialSituationTextView.setText(mood.getSocialSituation() != null
                ? mood.getSocialSituation().toString() : "Unknown");
        triggerTextView.setText(mood.getTrigger() != null
                ? mood.getTrigger() : "No trigger provided");
        // This shows the owner of the post.
        usernameTextView.setText(mood.getUsername() != null
                ? mood.getUsername() : "Unknown");
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

        // Load the mood photo using Glide into the placeholder ImageView if a photo URL exists.
        ImageView placeholderImageView = findViewById(R.id.imageview_placeholder);
        if (mood.getPhotoUrl() != null && !mood.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(mood.getPhotoUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(placeholderImageView);
        } else {
            // Optionally, set the default placeholder image if no photo exists.
            placeholderImageView.setImageResource(R.drawable.ic_placeholder);
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

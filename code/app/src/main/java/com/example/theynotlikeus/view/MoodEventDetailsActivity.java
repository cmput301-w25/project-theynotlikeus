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
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;

/**
 * Activity for displaying the details of a mood event.
 *
 * Retrieves mood data from Firestore and updates the UI.
 * Displays social situation, trigger, mood state, and date.
 * Allows navigation to edit or delete the mood entry.
 * Provides a back button to return to the main screen.
 */

public class MoodEventDetailsActivity extends AppCompatActivity {

    //Initiating UI elements

    TextView socialSituationTextView;
    TextView dateTextView;
    TextView triggerTextView;
    TextView usernameTextView;
    ImageView moodImageView;
    ImageButton backButton;
    ImageButton editButton;

    private MoodController moodController;
    private String moodId;//Mood document ID from Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);
        //Initialize Firestore database
        moodController = new MoodController();
        //Retrieve the mood ID passed from the previous activity
        moodId = getIntent().getStringExtra("moodId");

        //Bind UI elements to their respective views
        socialSituationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityMoodEventDetails_dateandtime);
        triggerTextView = findViewById(R.id.textview_ActivityMoodEventDetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_ActivityMoodEventDetails_username);
        moodImageView = findViewById(R.id.imageview_ActivityMoodEventDetails_moodimage);
        editButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_editbutton);
        backButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_backbutton);


        Log.d("MoodDetails", "onCreate: Mood ID = " + moodId);

        //Set up button listeners for edit and back buttons
        editButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Edit button clicked, moodId: " + moodId);
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("moodId", moodId);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            // Navigate back to MainActivity (or previous screen)
            Intent intent = new Intent(MoodEventDetailsActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fetch and update the mood data every time the activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MoodDetails", "onResume called, refreshing mood data");
        loadMoodData();
    }

    /**
     * Loads mood data from Firestore and updates the UI.
     */
    private void loadMoodData() {
        moodController.getMood(moodId, mood -> {
            // Update UI elements with the latest data
            socialSituationTextView.setText(mood.getSocialSituation() != null
                    ? mood.getSocialSituation().toString() : "Unknown");
            triggerTextView.setText(mood.getTrigger() != null
                    ? mood.getTrigger() : "No trigger provided");
            usernameTextView.setText(mood.getMoodState() != null
                    ? mood.getMoodState().toString() : "Unknown");

            // Display the mood icon
            int iconRes = getMoodIcon(mood.getMoodState());
            moodImageView.setImageResource(iconRes);

            // Update date field
            dateTextView.setText(mood.getDateTime() != null ? mood.getDateTime().toString() : "Unknown");
        }, error -> {
            Toast.makeText(MoodEventDetailsActivity.this, "Error loading mood: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Returns the appropriate icon resource for a given mood state.
     *
     * @param moodState The mood state to get an icon for.
     * @return The resource ID of the corresponding mood icon.
     */
    private int getMoodIcon(Mood.MoodState moodState) {
        if (moodState == null) {
            return R.drawable.ic_happy_emoticon; // Default icon
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
}

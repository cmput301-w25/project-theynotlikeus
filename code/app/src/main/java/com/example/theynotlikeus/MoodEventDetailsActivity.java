package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
*   Activity that displays the details of a mood event.
* */
public class MoodEventDetailsActivity extends AppCompatActivity {

    TextView socialSituationTextView;
    TextView dateTextView;
    TextView triggerTextView;
    TextView usernameTextView;
    ImageView moodImageView;
    ImageButton backButton;
    ImageButton editButton;

    private FirebaseFirestore db;
    private String moodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);

        db = FirebaseFirestore.getInstance();
        moodId = getIntent().getStringExtra("moodId");

        socialSituationTextView = findViewById(R.id.textview_ActivityMoodEventDetails_socialsituation);
        dateTextView = findViewById(R.id.textview_ActivityMoodEventDetails_date);
        triggerTextView = findViewById(R.id.textview_ActivityMoodEventDetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_ActivityMoodEventDetails_username);
        moodImageView = findViewById(R.id.imageview_ActivityMoodEventDetails_moodimage);
        editButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_editbutton);
        backButton = findViewById(R.id.imagebutton_ActivityMoodEventDetails_backbutton);

        // Log the moodId
        Log.d("MoodDetails", "onCreate: Mood ID = " + moodId);

        // Set up button listeners (edit and back remain unchanged)
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

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the latest mood data each time the activity resumes.
        Log.d("MoodDetails", "onResume called, refreshing mood data");
        loadMoodData();
    }

    // Method to load mood data from Firestore and update the UI.
    private void loadMoodData() {
        db.collection("moods").document(moodId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Mood mood = document.toObject(Mood.class);
                            if (mood != null) {
                                // DEBUG: Check if the strings below are actually set
                                // Update UI elements with the latest data
                                socialSituationTextView.setText(mood.getSocialSituation() != null
                                        ? mood.getSocialSituation().toString() : "Unknown");
                                triggerTextView.setText(mood.getTrigger() != null
                                        ? mood.getTrigger() : "No trigger provided");
                                // Assuming usernameTextView displays the mood state for this example
                                usernameTextView.setText(mood.getMoodState() != null
                                        ? mood.getMoodState().toString() : "Unknown");

                                // Optionally, display the mood icon
                                int iconRes = getMoodIcon(mood.getMoodState());
                                moodImageView.setImageResource(iconRes);

                                // If you have a date field, update it accordingly:
                                dateTextView.setText(mood.getDateTime() != null ? mood.getDateTime().toString() : "Unknown");
                            }
                        } else {
                            Toast.makeText(MoodEventDetailsActivity.this, "Mood not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MoodEventDetailsActivity.this,
                                "Error loading mood: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to return the appropriate icon resource for a given mood state.
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

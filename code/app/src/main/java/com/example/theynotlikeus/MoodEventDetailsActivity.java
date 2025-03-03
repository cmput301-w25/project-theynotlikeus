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
 * Activity that displays the details such as social situation, trigger, mood state, date, and an associated mood iconof a mood event
 * by retrieving a mood event from Firestore using a provided moodId.
 *
 * Allows the user to navigate to an edit screen or back to the main activity.
 */

public class MoodEventDetailsActivity extends AppCompatActivity {

    //UI elements for displaying mood details.
    TextView socialSituationTextView;
    TextView dateTextView;
    TextView triggerTextView;
    TextView usernameTextView;
    ImageView moodImageView;
    ImageButton backButton;
    ImageButton editButton;

    //Firebase Firestore instance.
    private FirebaseFirestore db;
    //The ID of the mood event document in Firestore.
    private String moodId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);


        db = FirebaseFirestore.getInstance();//Initialize Firestore instance.

        //Retrieve the moodId from the Intent extras.
        moodId = getIntent().getStringExtra("moodId");

        //Bind UI elements to their respective views for implementation
        socialSituationTextView = findViewById(R.id.textview_activitymoodeventdetails_socialsituation);
        dateTextView = findViewById(R.id.textview_activitymoodeventdetails_date);
        triggerTextView = findViewById(R.id.textview_activitymoodeventdetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_activitymoodeventdetails_username);
        moodImageView = findViewById(R.id.imageview_activitymoodeventdetails_moodimage);
        editButton = findViewById(R.id.imagebutton_activitymoodeventdetails_editbutton);
        backButton = findViewById(R.id.imagebutton_activitymoodeventdetails_backbutton);

        //Log the moodId for debugging purposes.
        Log.d("MoodDetails", "onCreate: Mood ID = " + moodId);

        //Set up the edit button listener to navigate to EditDeleteMoodActivity.
        editButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Edit button clicked, moodId: " + moodId);
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("moodId", moodId);
            startActivity(intent);
        });

        //Set up the back button listener to navigate back to MainActivity.
        backButton.setOnClickListener(v -> {
            // Create an Intent to return to MainActivity with a specific fragment loaded.
            Intent intent = new Intent(MoodEventDetailsActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "homeMyMoodsFrag");
            // Clear the activity stack to prevent returning to this screen.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Called when the activity resumes.
     * Refreshes the mood data by loading the latest information from Firestore.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MoodDetails", "onResume called, refreshing mood data");
        loadMoodData();
    }

    /**
     * Loads mood data from Firestore using the moodId and updates the UI.
     */
    private void loadMoodData() {
        //Access the "moods" collection and retrieve the document with the specified moodId.
        db.collection("moods").document(moodId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Convert the document snapshot into a Mood object.
                            Mood mood = document.toObject(Mood.class);
                            if (mood != null) {
                                //Update the social situation TextView.
                                socialSituationTextView.setText(mood.getSocialSituation() != null
                                        ? mood.getSocialSituation().toString() : "Unknown");
                                //Update the trigger TextView.
                                triggerTextView.setText(mood.getTrigger() != null
                                        ? mood.getTrigger() : "No trigger provided");
                                //Display the mood state in the usernameTextView
                                usernameTextView.setText(mood.getMoodState() != null
                                        ? mood.getMoodState().toString() : "Unknown");

                                //Get and set the appropriate mood icon.
                                int iconRes = getMoodIcon(mood.getMoodState());
                                moodImageView.setImageResource(iconRes);

                                //Update the date TextView, or set to "Unknown" if not available.
                                dateTextView.setText(mood.getDateTime() != null ? mood.getDateTime().toString() : "Unknown");
                            }
                        } else {
                            //Error handling if the mood is not found
                            Toast.makeText(MoodEventDetailsActivity.this, "Mood not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MoodEventDetailsActivity.this,
                                "Error loading mood: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Returns the appropriate icon resource for a given mood state.
     *
     * @param moodState the mood state for which the icon is required.
     * @return the drawable resource ID corresponding to the mood state.
     */
    private int getMoodIcon(Mood.MoodState moodState) {
        if (moodState == null) {
            return R.drawable.ic_happy_emoticon; // Default icon if mood state is null.
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
            case BOREDOM:
                return R.drawable.ic_bored_emoticon;
            default:
                return R.drawable.ic_happy_emoticon;
        }
    }
}

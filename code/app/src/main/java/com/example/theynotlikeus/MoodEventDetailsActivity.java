package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        socialSituationTextView = findViewById(R.id.textview_activitymoodeventdetails_socialsituation);
        dateTextView = findViewById(R.id.textview_activitymoodeventdetails_date);
        triggerTextView = findViewById(R.id.textview_activitymoodeventdetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_activitymoodeventdetails_username);
        moodImageView = findViewById(R.id.imageview_activitymoodeventdetails_moodimage);

        editButton = findViewById(R.id.imagebutton_activitymoodeventdetails_editbutton);
        backButton = findViewById(R.id.imagebutton_activitymoodeventdetails_backbutton);

        moodId = getIntent().getStringExtra("moodId");

        //gets mood details using the mood id from the database
        db.collection("moods").document(moodId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Mood mood = document.toObject(Mood.class);
                if (mood != null) {
                    socialSituationTextView.setText(mood.getSocialSituation() != null
                            ? mood.getSocialSituation().toString() : "Unknown");
                    triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger provided");
                    usernameTextView.setText(mood.getMoodState() != null
                            ? mood.getMoodState().toString() : "Unknown");

                    //display the mood icon
                    int iconRes = getMoodIcon(mood.getMoodState());
                    moodImageView.setImageResource(iconRes);
                }
            }
        });

        editButton.setOnClickListener(v -> {
            Log.d("MoodEventDetailsActivity", "Edit button clicked, moodId: " + moodId);
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("moodId", moodId); // Pass the unique ID of the mood for editing
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodEventDetailsActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "homeMyMoodsFrag");
            // These flags ensure that MainActivity is resumed rather than recreated
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }


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
            case BOREDOM:
                return R.drawable.ic_bored_emoticon;
            default:
                return R.drawable.ic_happy_emoticon;
        }
    }
}

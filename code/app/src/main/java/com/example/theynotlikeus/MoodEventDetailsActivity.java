package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;


public class MoodEventDetailsActivity extends AppCompatActivity {

    TextView moodTitleTextView;
    TextView socialSituationTextView;
    TextView dateTextView;
    TextView triggerTextView;
    TextView usernameTextView;

    ImageButton backButton;

    ImageButton editButton;

    private FirebaseFirestore db;
    private String moodId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_event_details);
        db = FirebaseFirestore.getInstance();
        //moodTitleTextView = findViewById(R.id.textView_moodTitle);
        socialSituationTextView = findViewById(R.id.textview_activitymoodeventdetails_socialsituation);
        dateTextView = findViewById(R.id.textview_activitymoodeventdetails_date);
        triggerTextView = findViewById(R.id.textview_activitymoodeventdetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_activitymoodeventdetails_username);
        backButton=findViewById(R.id.imagebutton_activitymoodeventdetails_backbutton);
        editButton = findViewById(R.id.imagebutton_activitymoodeventdetails_editbutton);

        /*
        //Get data from the last fragment
        String moodState = getIntent().getStringExtra("moodState");
        String socialSituation = getIntent().getStringExtra("socialSituation");
        //long dateTimeMillis = getIntent().getLongExtra("dateTime", 0);
        String reason = getIntent().getStringExtra("reason");
        String trigger = getIntent().getStringExtra("trigger");
        String username = getIntent().getStringExtra("username");
*/
        moodId=getIntent().getStringExtra("moodId");
            // Access the specific document by its ID
        db.collection("moods").document(moodId).get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            Mood mood = document.toObject(Mood.class);

            //moodStateText.setText(mood.getMoodState() != null ? mood.getMoodState().toString() : "Unknown");
            socialSituationTextView.setText(mood.getSocialSituation() != null ? mood.getSocialSituation().toString() : "Unknown");
            //reasonText.setText(mood.getReason() != null ? mood.getReason() : "No reason provided");
            triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger provided");
            usernameTextView.setText(mood.getUsername() != null ? mood.getUsername() : "Unknown");

            });
        /*
        //moodTitleTextView.setText(moodState);
        socialSituationTextView.setText(socialSituationTextView);
        triggerTextView.setText(trigger);
        usernameTextView.setText(username);
*/
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodEventDetailsActivity.this, EditDeleteMoodActivity.class);
            intent.putExtra("moodId", moodId); // Pass the unique ID of the mood for editing
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodEventDetailsActivity.this,homeMyMoodsFrag.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finish current activity so user doesn't return here on back press
        });





    }




    }


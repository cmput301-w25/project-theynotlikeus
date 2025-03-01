package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MoodEventDetailsActivity extends AppCompatActivity {

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

        socialSituationTextView = findViewById(R.id.textview_activitymoodeventdetails_socialsituation);
        dateTextView = findViewById(R.id.textview_activitymoodeventdetails_date);
        triggerTextView = findViewById(R.id.textview_activitymoodeventdetails_triggervalue);
        usernameTextView = findViewById(R.id.textview_activitymoodeventdetails_username);

        editButton = findViewById(R.id.imagebutton_activitymoodeventdetails_editbutton);
        backButton = findViewById(R.id.imagebutton_activitymoodeventdetails_backbutton); // Initialize backButton

        moodId = getIntent().getStringExtra("moodId");

        // Access the specific document by its ID
        db.collection("moods").document(moodId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Mood mood = document.toObject(Mood.class);
                if (mood != null) {
                    socialSituationTextView.setText(mood.getSocialSituation() != null ? String.valueOf(mood.getSocialSituation()) : "Unknown");
                    triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger provided");
                    usernameTextView.setText(mood.getUsername() != null ? mood.getUsername() : "Unknown");
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
}

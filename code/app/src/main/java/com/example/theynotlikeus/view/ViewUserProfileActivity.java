package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theynotlikeus.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class ViewUserProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private Button followButton;
    private FirebaseFirestore db;
    private String currentUser; // Replace with the logged-in user's username
    private String viewedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_followed);

        usernameTextView = findViewById(R.id.textView_UserProfileDetailsFrag_Followed_Username);
        followButton = findViewById(R.id.button_user_followed_follow);
        db = FirebaseFirestore.getInstance();

        viewedUser = getIntent().getStringExtra("username");
        currentUser = getIntent().getStringExtra("loggedInUser");
        usernameTextView.setText(viewedUser);

        checkFollowRequestStatus();

        findViewById(R.id.button_UserProfileDetailsFrag_Followed_backbutton).setOnClickListener(v -> finish());

        followButton.setOnClickListener(v -> sendFollowRequest());
    }

    private void checkFollowRequestStatus() {
        // Check if user is already followed
        db.collection("follow")
                .whereEqualTo("followee", viewedUser)
                .whereEqualTo("follower", currentUser)
                .get()
                .addOnSuccessListener(followSnapshot -> {
                    if (!followSnapshot.isEmpty()) {
                        // User is already followed, disable follow request
                        followButton.setText("Following");
                        followButton.setEnabled(false);
                    } else {
                        // If not followed, check if a request is pending
                        db.collection("request")
                                .whereEqualTo("followee", viewedUser)
                                .whereEqualTo("follower", currentUser)
                                .get()
                                .addOnSuccessListener(requestSnapshot -> {
                                    if (!requestSnapshot.isEmpty()) {
                                        // Request already sent
                                        followButton.setText("Requested");
                                        followButton.setEnabled(false);
                                    } else {
                                        // Allow sending request
                                        followButton.setText("Request Follow");
                                        followButton.setEnabled(true);
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error checking request status", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking follow status", Toast.LENGTH_SHORT).show());
    }

    private void sendFollowRequest() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("followee", viewedUser);
        requestData.put("follower", currentUser);
        //requestData.put("deci", "pending");

        db.collection("request").add(requestData)
                .addOnSuccessListener(documentReference -> {
                    followButton.setText("Requested");
                    followButton.setEnabled(false);
                    Toast.makeText(this, "Follow request sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show());
    }
}

package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theynotlikeus.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity displays the profile of another user
 *  and allows the user to send a follow request if they are not already following the viewed user.
 *
 * It also:
 * - Displays the viewed user's username.
 * - Checks if the logged-in user is already following or has requested to follow.
 */

public class ViewUserProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private Button followButton;
    private FirebaseFirestore db;

    private String currentUser;//The currently logged-in user's username

    private String viewedUser;//The user being viewed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followed);

        //Initialize UI components and Firestore
        usernameTextView=findViewById(R.id.textView_fragmentUserFollowed_Username);
        followButton=findViewById(R.id.button_fragmentUserFollowed_follow);
        db=FirebaseFirestore.getInstance();

        //Retrieve usernames passed through intent
        viewedUser=getIntent().getStringExtra("username");
        currentUser=getIntent().getStringExtra("loggedInUser");

        //Display the viewed user's name
        usernameTextView.setText(viewedUser);

        //Check if current user already follows the viewed user or has requested
        checkFollowRequestStatus();

        //Back button returns to previous activity
        findViewById(R.id.button_fragmentUserFollowed_backbutton).setOnClickListener(v -> finish());

        //Follow button click sends a follow request
        followButton.setOnClickListener(v -> sendFollowRequest());
    }

    /**
     * Checks Firestore to see if the user is already followed or if a follow request is pending.
     * Updates the follow button text accordingly.
     */
    private void checkFollowRequestStatus() {
        db.collection("follow")
                .whereEqualTo("followee", viewedUser)
                .whereEqualTo("follower", currentUser)
                .get()
                .addOnSuccessListener(followSnapshot -> {
                    if (!followSnapshot.isEmpty()) {
                        //User is already being followed
                        followButton.setText("Following");
                        followButton.setEnabled(false);
                    } else {
                        //Check if there's already a follow request pending
                        db.collection("request")
                                .whereEqualTo("followee", viewedUser)
                                .whereEqualTo("follower", currentUser)
                                .get()
                                .addOnSuccessListener(requestSnapshot -> {
                                    if (!requestSnapshot.isEmpty()) {
                                        //Follow request already sent
                                        followButton.setText("Requested");
                                        followButton.setEnabled(false);
                                    } else {
                                        //Follow request can be sent
                                        followButton.setText("Request Follow");
                                        followButton.setEnabled(true);
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error checking request status", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking follow status", Toast.LENGTH_SHORT).show());
    }

    /**
     * Sends a new follow request to the Firestore "request" collection.
     * Updates the UI upon success or failure.
     */
    private void sendFollowRequest() {
        Map<String, Object> requestData=new HashMap<>();
        requestData.put("followee", viewedUser);
        requestData.put("follower", currentUser);

        db.collection("request").add(requestData)
                .addOnSuccessListener(documentReference -> {
                    followButton.setText("Requested");
                    followButton.setEnabled(false);
                    Toast.makeText(this, "Follow request sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show());
    }
}

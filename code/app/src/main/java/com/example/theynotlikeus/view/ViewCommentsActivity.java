package com.example.theynotlikeus.view;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.CommentsRecyclerViewAdapter;
import com.example.theynotlikeus.controller.CommentController;
import com.example.theynotlikeus.model.Comment;
import com.example.theynotlikeus.model.Mood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This activity displays a list of comments associated with a specific Mood.
 * It also allows the users to:
 * - View all comments sorted by newest first
 * - Add a new comment using a DialogFragment
 */

public class ViewCommentsActivity extends AppCompatActivity implements AddCommentDialogFrag.AddCommentDialogListener {

    private Mood passedMood;
    private String username;
    private String moodId;

    private List<Comment> commentsList=new ArrayList<>();
    private RecyclerView commentsRecyclerView;
    private CommentsRecyclerViewAdapter commentsRecyclerViewAdapter;
    private RecyclerView.LayoutManager commentsRecyclerViewLayoutManager;

    private CommentController commentController;

    ImageButton backButton;
    ImageButton addCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);

        //Retrieve the mood and username from the Intent extras
        passedMood=(Mood) getIntent().getSerializableExtra("mood");
        String passedUsername=getIntent().getStringExtra("username");

        if (passedMood != null) {
            moodId=passedMood.getDocId();

            //Use passed username if available
            username=(passedUsername != null && !passedUsername.isEmpty()) ?
                    passedUsername : passedMood.getUsername();
        } else {
            //In case Mood object isn't passed, get moodId and username directly
            moodId = getIntent().getStringExtra("moodId");
            username = passedUsername;
        }

        //Set up RecyclerView to display comments
        commentsRecyclerView = findViewById(R.id.recyclerview_ViewCommentsActivity_commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(commentsRecyclerViewLayoutManager);
        commentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsRecyclerViewAdapter);

        //Initialize the controller
        commentController = new CommentController();

        //Back and add comment buttons
        backButton = findViewById(R.id.imagebutton_ActivityViewComments_backbutton);
        addCommentButton = findViewById(R.id.imagebutton_ActivityViewComments_addcommentsbutton);

        //navigate to MoodEventDetailsActivity when the back button is pressed
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewCommentsActivity.this, MoodEventDetailsActivity.class);
            startActivity(intent);
            finish();
        });

        //launch the AddCommentDialogFrag when the add comment button is pressed
        addCommentButton.setOnClickListener(v -> {
            AddCommentDialogFrag addCommentDialogFrag = new AddCommentDialogFrag();
            addCommentDialogFrag.show(getSupportFragmentManager(), "Add comment");
        });
    }

    /**
     * Reload comments from Firebase when the activity resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadCommentsFromFirebase();
    }

    /**
     * Callback method from AddCommentDialogFrag to receive and save a new comment.
     */
    @Override
    public void addComment(Comment comment) {
        //Set associated mood ID, author, and timestamp before saving
        comment.setAssociatedMoodID(moodId);
        comment.setCommentDateTime();
        comment.setCommentAuthor(username);

        //Add comment to Firestore through controller
        commentController.addComment(comment, () -> runOnUiThread(() -> {
            Toast.makeText(this, "Comment saved successfully!", Toast.LENGTH_SHORT).show();
            loadCommentsFromFirebase(); //Refresh list
        }), e -> runOnUiThread(() -> {
            Toast.makeText(this, "Error saving comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

    /**
     * Loads comments for the given mood ID and updates the RecyclerView.
     * Sorts the list by newest comment first.
     */
    private void loadCommentsFromFirebase() {
        Log.d("viewcomments", "you have entered");

        commentController.getCommentsByMoodID(moodId, comments -> {
            commentsList.clear();
            //Sort comments newest first
            Collections.sort(comments, (m1, m2) -> m2.getCommentDateTime().compareTo(m1.getCommentDateTime()));
            commentsList.addAll(comments);
            commentsRecyclerViewAdapter.notifyDataSetChanged();

            Log.i("ViewCommentsActivity", "Total comments fetched: " + commentsList.size());
        }, exception -> {
            Log.e("ViewCommentsActivity", "Error fetching comments", exception);
        });
    }
}

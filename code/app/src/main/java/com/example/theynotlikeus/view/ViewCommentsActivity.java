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
import java.util.Date;
import java.util.List;

public class ViewCommentsActivity extends AppCompatActivity implements AddCommentDialogFrag.AddCommentDialogListener {

    private Mood passedMood;
    private String username;
    private String moodId;
    private List<Comment> commentsList = new ArrayList<>();
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
        passedMood = (Mood) getIntent().getSerializableExtra("mood");

        if (passedMood != null) {
            moodId = passedMood.getDocId();
            username = passedMood.getUsername();
        } else {
            moodId = getIntent().getStringExtra("moodId");
            username = passedMood.getUsername();
        }

        commentsRecyclerView = findViewById(R.id.recyclerview_ViewCommentsActivity_commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(commentsRecyclerViewLayoutManager);
        commentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsRecyclerViewAdapter);
        commentController = new CommentController();
        backButton = findViewById(R.id.imagebutton_ActivityViewComments_backbutton);
        addCommentButton = findViewById(R.id.imagebutton_ActivityViewComments_addcommentsbutton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewCommentsActivity.this, MoodEventDetailsActivity.class);
            startActivity(intent);
            finish();
        });

        addCommentButton.setOnClickListener(v -> {
        AddCommentDialogFrag addCommentDialogFrag = new AddCommentDialogFrag();
        addCommentDialogFrag.show(getSupportFragmentManager(), "Add comment");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCommentsFromFirebase();
    }

    @Override
    public void addComment(Comment comment) {
        comment.setAssociatedMoodID(moodId);
        comment.setCommentDateTime();
        comment.setCommentAuthor(username);
        commentController.addComment(comment, () -> runOnUiThread(() -> {
            Toast.makeText(this, "Comment saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }), e -> runOnUiThread(() -> {
            Toast.makeText(this, "Error saving comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }


    private void loadCommentsFromFirebase() {
        Log.d("viewcomments", "you have entered");
        commentController.getCommentsByMoodID(moodId, comments -> {
            commentsList.clear();
            Collections.sort(comments, (m1, m2) -> m2.getCommentDateTime().compareTo(m1.getCommentDateTime()));
            commentsList.addAll(comments);
            commentsRecyclerViewAdapter.notifyDataSetChanged();
            Log.i("ViewCommentsActivity", "Total comments fetched: " + commentsList.size());
        },
                exception -> {
            Log.e("ViewCommentsActivity", "Error fetching comments", exception);
        });
    }
}

package com.example.theynotlikeus.view;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.CommentsRecyclerViewAdapter;
import com.example.theynotlikeus.controller.CommentController;
import com.example.theynotlikeus.model.Comment;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewCommentsActivity extends AppCompatActivity {

    private String moodId;
    private List<Comment> commentsList = new ArrayList<>();
    private RecyclerView commentsRecyclerView;
    private CommentsRecyclerViewAdapter commentsRecyclerViewAdapter;
    private RecyclerView.LayoutManager commentsRecyclerViewLayoutManager;
    private CommentController commentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);

        moodId = getIntent().getStringExtra("moodId");      // Add the username into the intent when going to this activity
        commentsRecyclerView = findViewById(R.id.recyclerview_ViewCommentsActivity_commmentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(commentsRecyclerViewLayoutManager);
        commentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsRecyclerViewAdapter);

        commentController = new CommentController();
        loadCommentsFromFirebase();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCommentsFromFirebase();

    }

    private void loadCommentsFromFirebase() {
        commentsList.clear();
        commentController.getCommentsByMoodID(moodId, comments -> {
            commentsList.addAll(comments);
            Collections.sort(commentsList, (c1, c2) -> c2.getCommentDateTime().compareTo(c1.getCommentDateTime()));
            Log.i("ViewCommentsActivity", "Total moods fetched: " + commentsList.size());
        },
                exception -> {
            Log.e("ViewCommentsActivity", "Error fetching comments", exception);
        });
    }
}

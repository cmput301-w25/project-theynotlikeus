package com.example.theynotlikeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Comment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This adapter is used to display a list of Comment objects in a RecyclerView
 * With each item showing the author, text and timestamp of the comment
 */
public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentsViewHolder> {

    private List<Comment> commentList;
    private Context context;


    public CommentsRecyclerViewAdapter(Context context, List<Comment> commentList) {
        this.commentList = commentList;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder.
     */
    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_view_comments_comment_layout, parent, false);
        return new CommentsViewHolder(view);


    }

    /**
     * Returns the total number of items to display.
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }


    /**
     * Binds data to the view elements for each comment item.
     */
    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position); //Get comment at the current position

        //Set comment author and text
        holder.commentAuthor.setText(comment.getCommentAuthor());
        holder.commentText.setText(comment.getCommentText());

        //Format and set the comment date/time
        if (comment.getCommentDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.commentDateTime.setText(dateFormat.format(comment.getCommentDateTime()));
        } else {
            holder.commentDateTime.setText("Unknown");

        }

    }


    /**
     * ViewHolder class to hold references to the UI components for each comment item.
     */
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor;
        TextView commentText;
        TextView commentDateTime;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            commentAuthor = itemView.findViewById(R.id.textview_ViewCommentsActivityCommentLayout_commentAuthor);
            commentText = itemView.findViewById(R.id.textview_ViewCommentsActivityCommentLayout_commentText);
            commentDateTime = itemView.findViewById(R.id.textview_ViewCommentsActivityCommentLayout_dateTime);
        }

    }

}

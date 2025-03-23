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

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentsViewHolder> {

    private List<Comment> commentList;
    private Context context;

    public CommentsRecyclerViewAdapter(Context context, List<Comment> commentList) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_comments_comment_layout, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position);    // Get comment at position
        holder.commentAuthor.setText(comment.getCommentAuthor());
        holder.commentText.setText(comment.getCommentText());
        if (comment.getCommentDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.commentDateTime.setText(dateFormat.format(comment.getCommentDateTime()));
        } else {
            holder.commentDateTime.setText("Unknown");
        }
    }


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

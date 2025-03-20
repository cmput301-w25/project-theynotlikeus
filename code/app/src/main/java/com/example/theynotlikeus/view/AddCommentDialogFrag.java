package com.example.theynotlikeus.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Comment;

import java.util.Date;

public class AddCommentDialogFrag extends DialogFragment {

    interface AddCommentDialogListener {
        void addComment(Comment comment);
    }
    private AddCommentDialogListener listener;

    public static AddCommentDialogFrag newInstance(Comment comment) {
        Bundle args = new Bundle();
        args.putSerializable("Comment", comment);
        AddCommentDialogFrag fragment = new AddCommentDialogFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddCommentDialogListener){
            listener = (AddCommentDialogListener) context;
        }
        else {
            throw new RuntimeException("Implement listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_add_comments_dialog_frag, null);
        EditText editCommentText = view.findViewById(R.id.edittext_FragmentAddCommentsDialogFrag_editCommentText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add a comment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String commentText = editCommentText.getText().toString();
                    listener.addComment(new Comment(commentText, new Date()));
                })
                .create();
    }
}

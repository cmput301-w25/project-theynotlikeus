package com.example.theynotlikeus.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Comment;

import java.util.Date;

/**
 * This class represents a DialogFragment that allows users to add a comment. It returns a comment object.
 */
public class AddCommentDialogFrag extends DialogFragment {
    private String background_color = "#212020";

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

        /* Change title of dialog fragment: https://stackoverflow.com/questions/51380128/android-how-can-i-change-alertdialog-title-text-color-and-background-color-with
         * Author: Anisuzzaman Babla
         * Taken by: Ercel Angeles
         * Taken on: March 22, 2025
         */
        TextView customTitle = new TextView(getContext());
        customTitle.setText("Add a comment");
        customTitle.setTextColor(Color.WHITE);
        customTitle.setTextSize(20);
        customTitle.setPadding(32, 32, 32, 32);
        customTitle.setGravity(Gravity.CENTER);
        customTitle.setTextColor(Color.parseColor("#FDCFF3"));
        builder.setCustomTitle(customTitle);


        return builder
                .setView(view)
                .setTitle("Add a comment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Post", (dialog, which) -> {
                    String commentText = editCommentText.getText().toString();
                    listener.addComment(new Comment(commentText, new Date()));
                })
                .create();
    }

    /* Getting dialog: https://stackoverflow.com/questions/8456143/dialogfragment-getdialog-returns-null
     * Author: Peter Ajtai
     * Taken by: Ercel Angeles
     * Taken on: March 22, 2025
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog alertDialog = getDialog();

        if (alertDialog != null && alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor(background_color)));
        }

        if (alertDialog instanceof AlertDialog) {
            /* Change alert dialog color: https://stackoverflow.com/questions/4095758/change-button-color-in-alertdialog
             * Author: Blacklight
             * Taken by: Ercel Angeles
             * Taken on: March 22, 2025
             */
            Button negativeButton = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            Button positiveButton = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);


            negativeButton.setTextColor(Color.parseColor("#FDCFF3"));
            positiveButton.setTextColor(Color.parseColor("#FDCFF3"));
        }
    }
}

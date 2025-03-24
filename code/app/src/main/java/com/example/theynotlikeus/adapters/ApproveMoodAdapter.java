package com.example.theynotlikeus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ApproveMoodAdapter extends RecyclerView.Adapter<ApproveMoodAdapter.MoodViewHolder> {

    // Callback interface for button actions.
    public interface OnMoodActionListener {
        void onApprove(Mood mood);
        void onDelete(Mood mood);
    }

    private List<Mood> moodList;
    private OnMoodActionListener actionListener;
    // Date formatter to format the mood event date/time.
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    public ApproveMoodAdapter(List<Mood> moodList, OnMoodActionListener actionListener) {
        this.moodList = moodList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approve_mood, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);
        // Use placeholder if username is missing.
        String username = (mood.getUsername() != null && !mood.getUsername().isEmpty())
                ? mood.getUsername() : "Sample User";
        holder.usernameTextView.setText(username);

        // Compose a mood description using the mood state and trigger.
        String moodDescription = (mood.getMoodState() != null)
                ? mood.getMoodState().toString() : "Sample Mood";
        if (mood.getTrigger() != null && !mood.getTrigger().isEmpty()) {
            moodDescription += " - " + mood.getTrigger();
        } else {
            moodDescription += " - Sample Trigger";
        }
        holder.moodTextView.setText(moodDescription);

        // Format and set the date/time if available.
        if (mood.getDateTime() != null) {
            String formattedDate = dateFormat.format(mood.getDateTime());
            holder.dateTimeTextView.setText(formattedDate);
        } else {
            holder.dateTimeTextView.setText("Unknown date");
        }

        // Set click listeners for the buttons.
        holder.approveButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onApprove(mood);
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(mood);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moodList != null ? moodList.size() : 0;
    }

    // Call this method to update the list of moods.
    public void updateMoodList(List<Mood> moodList) {
        this.moodList = moodList;
        notifyDataSetChanged();
    }

    // ViewHolder to hold item views.
    static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView moodTextView;
        TextView dateTimeTextView;
        Button approveButton;
        Button deleteButton;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textView_username);
            moodTextView = itemView.findViewById(R.id.textView_moodText);
            dateTimeTextView = itemView.findViewById(R.id.textView_dateTime); // Make sure this ID exists in your layout.
            approveButton = itemView.findViewById(R.id.button_approve);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}

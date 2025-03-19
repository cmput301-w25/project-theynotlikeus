package com.example.theynotlikeus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommunityRecyclerViewAdapter
        extends RecyclerView.Adapter<CommunityRecyclerViewAdapter.ViewHolder> {

    private final List<Mood> moodList;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    public CommunityRecyclerViewAdapter(List<Mood> moodList) {
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_community_mood, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        Mood mood = moodList.get(position);
        holder.bind(mood);
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, moodStateTextView, triggerTextView, dateTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textView_itemCommunity_username);
            moodStateTextView = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_moodtitle);
            triggerTextView   = itemView.findViewById(R.id.textView_itemCommunity_trigger);
            dateTextView      = itemView.findViewById(R.id.textView_itemCommunity_date);
        }

        void bind(Mood mood) {
            usernameTextView.setText(mood.getUsername());
            moodStateTextView.setText(mood.getMoodState().name());
            triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "");
            if (mood.getDateTime() != null) {
                dateTextView.setText(DATE_FORMAT.format(mood.getDateTime()));
            } else {
                dateTextView.setText("");
            }
        }
    }
}

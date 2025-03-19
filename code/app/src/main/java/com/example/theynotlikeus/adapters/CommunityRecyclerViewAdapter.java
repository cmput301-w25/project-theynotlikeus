package com.example.theynotlikeus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        ImageView moodIconImageView;
        TextView usernameTextView, moodStateTextView, triggerTextView, dateTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Retrieve views using IDs from the Community layout
            moodIconImageView = itemView.findViewById(R.id.imageView_itemCommunity_moodicon);
            usernameTextView = itemView.findViewById(R.id.textView_itemCommunity_username);
            moodStateTextView = itemView.findViewById(R.id.textView_itemCommunity_moodtitle);
            triggerTextView   = itemView.findViewById(R.id.textView_itemCommunity_trigger);
            dateTextView      = itemView.findViewById(R.id.textView_itemCommunity_date);
        }

        void bind(Mood mood) {
            // Set username (fallback to "Unknown" if null)
            usernameTextView.setText(mood.getUsername() != null ? mood.getUsername() : "Unknown");

            // Set mood state title
            moodStateTextView.setText(mood.getMoodState().name());

            // Set trigger text, if available
            triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "");

            // Set formatted date or empty string if null
            if (mood.getDateTime() != null) {
                dateTextView.setText(DATE_FORMAT.format(mood.getDateTime()));
            } else {
                dateTextView.setText("");
            }

            // Set mood icon based on mood state
            int moodIconRes = getMoodIcon(mood.getMoodState());
            moodIconImageView.setImageResource(moodIconRes);
        }

        /**
         * Returns the appropriate icon resource based on the given mood state.
         *
         * @param moodState The mood state for which to get the icon.
         * @return The drawable resource id of the mood icon.
         */
        private int getMoodIcon(Mood.MoodState moodState) {
            if (moodState == null) {
                moodState = Mood.MoodState.HAPPINESS; // Default mood if null
            }
            switch (moodState) {
                case ANGER:
                    return R.drawable.ic_angry_emoticon;
                case CONFUSION:
                    return R.drawable.ic_confused_emoticon;
                case DISGUST:
                    return R.drawable.ic_disgust_emoticon;
                case FEAR:
                    return R.drawable.ic_fear_emoticon;
                case HAPPINESS:
                    return R.drawable.ic_happy_emoticon;
                case SADNESS:
                    return R.drawable.ic_sad_emoticon;
                case SHAME:
                    return R.drawable.ic_shame_emoticon;
                case SURPRISE:
                    return R.drawable.ic_surprised_emoticon;
                default:
                    return R.drawable.ic_happy_emoticon;
            }
        }
    }
}

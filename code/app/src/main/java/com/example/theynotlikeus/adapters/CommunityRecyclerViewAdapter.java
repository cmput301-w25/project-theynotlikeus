package com.example.theynotlikeus.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.view.FriendMoodEventDetailsActivity;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter used to display a list of public Mood events in a RecyclerView.
 * Each item shows:
 * - Mood icon based on mood state
 * - Username of the mood's owner
 * - Mood state title
 * - Optional trigger text
 * - Social situation (if provided)
 * - Formatted date/time
 */

public class CommunityRecyclerViewAdapter
        extends RecyclerView.Adapter<CommunityRecyclerViewAdapter.ViewHolder> {

    private final List<Mood> moodList;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    /**
     * Constructor for the adapter.
     *
     * @param moodList List of Mood objects to display.
     */
    public CommunityRecyclerViewAdapter(List<Mood> moodList) {
        this.moodList = moodList;
    }

    /**
     * Inflates the layout and creates a new ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_community_mood, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Binds data from a Mood object to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        Mood mood = moodList.get(position);
        holder.bind(mood);
    }

    /**
     * Returns the number of Mood items in the list.
     */
    @Override
    public int getItemCount() {
        return moodList.size();
    }

    /**
     * ViewHolder class representing one item in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moodIconImageView;
        TextView usernameTextView, moodStateTextView, triggerTextView, dateTextView, socialSituation;

        /**
         * Initializes all views from the layout.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //instantiating the items in the xml file
            moodIconImageView = itemView.findViewById(R.id.imageView_itemCommunity_moodicon);
            usernameTextView = itemView.findViewById(R.id.textView_itemCommunity_username);
            moodStateTextView = itemView.findViewById(R.id.textView_itemCommunity_moodtitle);
            triggerTextView   = itemView.findViewById(R.id.textView_itemCommunity_trigger);
            dateTextView      = itemView.findViewById(R.id.textView_itemCommunity_date);
            socialSituation   = itemView.findViewById(R.id.textView_itemCommunity_socialsituation);
        }

        /**
         * Binds the Mood data to the respective views in the layout.
         *
         * @param mood The Mood object to display.
         */
        void bind(Mood mood) {
            //Username fallback
            usernameTextView.setText(mood.getUsername() != null ? mood.getUsername() : "Unknown");

            //Mood state fallback
            moodStateTextView.setText(mood.getMoodState() != null ? mood.getMoodState().name() : "Unknown");

            //Social situation fallback
            socialSituation.setText(mood.getSocialSituation() != null
                    ? mood.getSocialSituation().toString()
                    : "Unknown");

            //Optional trigger text
            triggerTextView.setText(mood.getTrigger() != null ? mood.getTrigger() : "");

            //Format and set date
            if (mood.getDateTime() != null) {
                dateTextView.setText(DATE_FORMAT.format(mood.getDateTime()));
            } else {
                dateTextView.setText("");
            }

            //Set the correct mood icon
            int moodIconRes = getMoodIcon(mood.getMoodState());
            moodIconImageView.setImageResource(moodIconRes);

            //Launch detail view when item is clicked
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), FriendMoodEventDetailsActivity.class);
                intent.putExtra("mood", mood); //Ensure Mood is Serializable or Parcelable
                v.getContext().startActivity(intent);
            });
        }

        /**
         * Returns the appropriate drawable resource ID for a given MoodState.
         *
         * @param moodState The mood state.
         * @return Drawable resource ID.
         */
        private int getMoodIcon(Mood.MoodState moodState) {
            if (moodState == null) {
                moodState = Mood.MoodState.HAPPINESS; //Default fallback icon
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

package com.example.theynotlikeus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/*
 * Adapter for displaying a list of Mood objects in a RecyclerView.
 *
 * This adapter:
 * Binds Mood data to the corresponding UI components defined in the layout.
 * Formats and displays mood details, such as mood state, social situation, and date.
 * Supports item click events via the OnItemClickListener interface.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.MyViewHolder> {
    private List<Mood> userMoodList; //List of Mood objects to be displayed in the RecyclerView.
    private Context context;//Context from the calling Activity or Fragment.


    /**
     * Interface definition for a callback to be invoked when a mood item is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(Mood mood);
    }

    private OnItemClickListener listener;//Listener instance for handling item click events.

    /**
     * Constructor for the adapter.
     *
     * @param context      the context where the adapter is being used.
     * @param userMoodList the list of Mood objects to display.
     */
    public UserRecyclerViewAdapter(Context context, List<Mood> userMoodList) {
        this.userMoodList = userMoodList;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link MyViewHolder} of the given type.
     *
     * @param parent   the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     * @return a new MyViewHolder that holds the View for a mood item.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout for individual mood items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mood_event_layout, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   the ViewHolder which should be updated to represent the contents of the item.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mood mood = userMoodList.get(position);//Retrieve the Mood object at the specified position.

        //Determine the appropriate mood icon. Default to SURPRISE if mood state is null.
        int moodIconRes = getMoodIcon(mood.getMoodState() != null ? mood.getMoodState() : Mood.MoodState.SURPRISE);
        holder.imageViewMoodIcon.setImageResource(moodIconRes);

        //Populating with mood details

        //Set mood state title; if mood state is null, display "Unknown".
        holder.textViewMoodTitle.setText(mood.getMoodState() != null ? mood.getMoodState().toString() : "Unknown");

        //Set social situation; if null, display "Unknown".
        holder.textViewSocialSituation.setText(mood.getSocialSituation() != null ? mood.getSocialSituation().toString() : "Unknown");

        //Format and set the date for the mood event; if date is null, display "Unknown".
        if (mood.getDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.textViewDate.setText(dateFormat.format(mood.getDateTime()));
        } else {
            holder.textViewDate.setText("Unknown");
        }

        //Set a click listener on the item view to trigger the OnItemClickListener.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                //Ensure the position is valid before handling the click.
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(userMoodList.get(pos));
                }
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the size of the userMoodList.
     */
    @Override
    public int getItemCount() {
        return userMoodList.size();
    }

    /**
     * ViewHolder class for holding references to the UI components for each mood item.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMoodIcon;
        TextView textViewMoodTitle;
        TextView textViewSocialSituation;
        TextView textViewDate;

        /**
         * Constructor for MyViewHolder.
         *
         * @param itemView the view representing a single mood item.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Initialize UI components in order to implement them
            imageViewMoodIcon = itemView.findViewById(R.id.imageview_FragmentMoodEventLayout_moodicon);
            textViewMoodTitle = itemView.findViewById(R.id.textview_FragmentMoodEventLayout_moodtitle);
            textViewSocialSituation = itemView.findViewById(R.id.textview_FragmentMoodEventLayout_socialsituation);
            textViewDate = itemView.findViewById(R.id.textview_FragmentMoodEventLayout_date);
        }
    }

    /**
     * Retrieves the drawable resource ID for the corresponding MoodState.
     *
     * @param moodState the MoodState for which the icon is needed.
     * @return the drawable resource ID representing the mood icon.
     */
    private int getMoodIcon(Mood.MoodState moodState) {
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
            case BOREDOM:
                return R.drawable.ic_bored_emoticon;
            default:
                //Return a default icon if the mood state doesn't match any known type.
                return R.drawable.ic_happy_emoticon;
        }
    }

    /**
     * Sets the OnItemClickListener for handling item click events.
     *
     * @param listener the listener to be notified when an item is clicked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

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

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.MyViewHolder> {
    private List<Mood> userMoodList;
    private Context context;

    // Define an interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(Mood mood);
    }

    private OnItemClickListener listener;

    public UserRecyclerViewAdapter(Context context, List<Mood> userMoodList) {
        this.userMoodList = userMoodList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mood_event_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mood mood = userMoodList.get(position);

        int moodIconRes = getMoodIcon(mood.getMoodState() != null ? mood.getMoodState() : Mood.MoodState.SURPRISE);
        holder.imageViewMoodIcon.setImageResource(moodIconRes);
        holder.textViewMoodTitle.setText(mood.getMoodState() != null ? mood.getMoodState().toString() : "Unknown");
        holder.textViewSocialSituation.setText(mood.getSocialSituation() != null ? mood.getSocialSituation().toString() : "Unknown");

        if (mood.getDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.textViewDate.setText(dateFormat.format(mood.getDateTime()));
        } else {
            holder.textViewDate.setText("Unknown");
        }

        // Set the click listener for the whole itemView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(userMoodList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userMoodList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMoodIcon;
        TextView textViewMoodTitle;
        TextView textViewSocialSituation;
        TextView textViewDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMoodIcon = itemView.findViewById(R.id.imageview_fragmentmoodeventlayout_moodicon);
            textViewMoodTitle = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_moodtitle);
            textViewSocialSituation = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_socialsituation);
            textViewDate = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_date);
        }
    }

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
                return R.drawable.ic_happy_emoticon; // Default icon
        }
    }
}

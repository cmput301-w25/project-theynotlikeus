package com.example.theynotlikeus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.Mood;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private Context context;
    private List<Mood> moodList;

    public MoodEventAdapter(Context context, List<Mood> moodList) {
        this.context = context;
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_mood_event_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mood mood = moodList.get(position);


        int moodIconRes = getMoodIcon(mood.getMoodState());
        holder.moodIcon.setImageResource(moodIconRes);

        holder.moodTitle.setText(mood.getMoodState().toString());
        holder.socialSituation.setText(mood.getSocialSituation() != null ? mood.getSocialSituation().toString() : "Unknown");


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.date.setText(dateFormat.format(mood.getDateTime()));
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moodIcon;
        TextView moodTitle, socialSituation, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodIcon = itemView.findViewById(R.id.imageview_fragmentmoodeventlayout_moodicon);
            moodTitle = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_moodtitle);
            socialSituation = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_socialsituation);
            date = itemView.findViewById(R.id.textview_fragmentmoodeventlayout_date);
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
                return R.drawable.ic_happy_emoticon; // Default
        }
    }
}

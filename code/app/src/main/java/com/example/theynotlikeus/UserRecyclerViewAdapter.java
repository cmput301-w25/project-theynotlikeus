package com.example.theynotlikeus;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import com.google.firebase.firestore.QuerySnapshot;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.MyViewHolder> {
    private List<Mood> userMoodList;
    private Context context;
    private FirebaseFirestore db;

    private Mood mood;

    public UserRecyclerViewAdapter(Context context, List<Mood> userMoodList) {
        this.userMoodList = userMoodList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mood_event_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AtomicReference<Mood> mood = new AtomicReference<>(userMoodList.get(position));

        int moodIconRes = getMoodIcon(mood.get().getMoodState() != null ? mood.get().getMoodState() : Mood.MoodState.SURPRISE);
        holder.imageViewMoodIcon.setImageResource(moodIconRes);
        //get photo, Unknown if null


        holder.textViewMoodTitle.setText(mood.get().getMoodState() != null ? mood.get().getMoodState().toString() : "Unknown");
        //get mood state, Unknown if null

        holder.textViewSocialSituation.setText(mood.get().getSocialSituation() != null ? mood.get().getSocialSituation().toString() : "Unknown");
        //get social situation, Unknown if null
        String username = mood.get().getUsername() != null ? mood.get().getUsername() : "Unknown";

        if (mood.get().getDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.textViewDate.setText(dateFormat.format(mood.get().getDateTime()));
        } else {
            holder.textViewDate.setText("Unknown");
        }//set date to the correct format, Unknown if null
//        if (mood.getDateTime() != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//            holder.textViewDate.setText(dateFormat.format(mood.getDateTime()));
//        } else {
//            holder.textViewDate.setText("Unknown");
//        }

            /*
            Intent intent = new Intent(context, MoodEventDetailsActivity.class);
            intent.putExtra("moodState", mood.getMoodState() != null ? mood.getMoodState().toString() : "Unknown");
            intent.putExtra("socialSituation", mood.getSocialSituation() != null ? mood.getSocialSituation().toString() : "Unknown");
            //intent.putExtra("dateTime", mood.getDateTime() != null ? mood.getDateTime().toDate().getTime() : 0);
            intent.putExtra("reason", mood.getReason() != null ? mood.getReason() : "No reason provided");
            intent.putExtra("trigger",mood.getTrigger()!=null?mood.getReason() : "No reason provided");
            intent.putExtra("username", mood.getUsername() != null ? mood.getUsername() : "Unknown");;
            context.startActivity(intent);*/

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
    }// find picture for each mood
}
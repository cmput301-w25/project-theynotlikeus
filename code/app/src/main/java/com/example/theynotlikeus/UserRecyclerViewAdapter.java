package com.example.theynotlikeus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.MyViewHolder> {
    private List<MoodEvent> userMoodEventList;
    private Context context;

    private FirebaseFirestore db;

    public UserRecyclerViewAdapter(Context context, List<MoodEvent> userMoodEventList) {
        this.userMoodEventList = userMoodEventList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mood_event_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageViewMoodIcon.setImageResource(R.drawable.ic_happy_emoticon);    // Needs to be changed
        holder.textViewMoodTitle.setText(userMoodEventList.get(position).getMoodEventEmotionalState());
        holder.textViewSocialSituation.setText(userMoodEventList.get(position).getMoodEventOptionalSocialSituation());
        holder.textViewDate.setText(userMoodEventList.get(position).getMoodEventDate());
    }

    @Override
    public int getItemCount() {
        return userMoodEventList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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


}

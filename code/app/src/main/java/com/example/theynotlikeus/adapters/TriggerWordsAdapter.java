package com.example.theynotlikeus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import java.util.List;

public class TriggerWordsAdapter extends RecyclerView.Adapter<TriggerWordsAdapter.TriggerWordViewHolder> {
    private List<String> triggerWords;

    public TriggerWordsAdapter(List<String> triggerWords) {
        this.triggerWords = triggerWords;
    }

    @NonNull
    @Override
    public TriggerWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trigger_word, parent, false);
        return new TriggerWordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TriggerWordViewHolder holder, int position) {
        String word = triggerWords.get(position);
        holder.textViewWord.setText(word);
    }

    @Override
    public int getItemCount() {
        return triggerWords.size();
    }

    public static class TriggerWordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWord;
        public TriggerWordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.textview_trigger_word);
        }
    }
}

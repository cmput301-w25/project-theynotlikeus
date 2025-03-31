package com.example.theynotlikeus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.TriggerWord;
import java.util.List;

/**
 * Adapter for displaying a list of trigger words with a delete button.
 */
public class TriggerWordsAdapter extends RecyclerView.Adapter<TriggerWordsAdapter.TriggerWordViewHolder> {

    /**
     * Listener for trigger word delete actions.
     */
    public interface OnTriggerWordActionListener {
        void onDelete(TriggerWord triggerWord);

    }

    private List<TriggerWord> triggerWords;
    private OnTriggerWordActionListener actionListener;

    public TriggerWordsAdapter(List<TriggerWord> triggerWords, OnTriggerWordActionListener actionListener) {
        this.triggerWords = triggerWords;
        this.actionListener = actionListener;

    }

    @NonNull
    @Override
    public TriggerWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trigger_word, parent, false);
        return new TriggerWordViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TriggerWordViewHolder holder, int position) {
        TriggerWord triggerWord = triggerWords.get(position);
        holder.textViewWord.setText(triggerWord.getWord());
        holder.buttonDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(triggerWord);
            }

        });

    }

    /**
     * Returns the number of trigger words.
     */
    @Override
    public int getItemCount() {
        return triggerWords != null ? triggerWords.size() : 0;
    }

    /**
     * Updates the trigger words list and refreshes the view.
     */
    public void updateTriggerWords(List<TriggerWord> words) {
        this.triggerWords = words;
        notifyDataSetChanged();

    }

    /**
     * ViewHolder for a trigger word item.
     */
    static class TriggerWordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWord;
        Button buttonDelete;
        public TriggerWordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.textview_trigger_word);
            buttonDelete = itemView.findViewById(R.id.button_TriggerWordsFrag_delete);

        }

    }

}

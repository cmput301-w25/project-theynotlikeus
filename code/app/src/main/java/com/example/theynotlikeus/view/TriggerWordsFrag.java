package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapters.TriggerWordsAdapter;
import com.example.theynotlikeus.controller.TriggerWordsController;
import com.example.theynotlikeus.model.TriggerWord;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that allows an admin user to:
 * - add trigger words
 * - edit/delete trigger words
 * (Trigger words are words that are words that the admin may want to censor from other users mood events)
 */
public class TriggerWordsFrag extends Fragment {

    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private EditText editTextNewTrigger;
    private Button buttonAddTrigger;
    private TriggerWordsAdapter adapter;
    private List<TriggerWord> triggerWordsList;
    private TriggerWordsController triggerWordsController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trigger_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonBack = view.findViewById(R.id.button_TriggerWordsFrag_back);
        recyclerView = view.findViewById(R.id.recyclerview_trigger_words);
        editTextNewTrigger = view.findViewById(R.id.edittext_TriggerWordsFrag_newTriggerword);
        buttonAddTrigger = view.findViewById(R.id.button_TriggerWordsFrag_add);

        triggerWordsList = new ArrayList<>();
        triggerWordsController = new TriggerWordsController();

        adapter = new TriggerWordsAdapter(triggerWordsList, triggerWord -> {
            // Delete trigger word callback.
            triggerWordsController.deleteTriggerWord(triggerWord.getId(), () -> {
                Toast.makeText(getContext(), "Trigger word deleted", Toast.LENGTH_SHORT).show();
                loadTriggerWords();
            }, e -> {
                Toast.makeText(getContext(), "Error deleting trigger word: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonAddTrigger.setOnClickListener(v -> {
            String newWord = editTextNewTrigger.getText().toString().trim();
            if (newWord.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a trigger word", Toast.LENGTH_SHORT).show();
                return;
            }
            triggerWordsController.addTriggerWord(newWord, () -> {
                Toast.makeText(getContext(), "Trigger word added", Toast.LENGTH_SHORT).show();
                editTextNewTrigger.setText("");
                loadTriggerWords();
            }, e -> {
                Toast.makeText(getContext(), "Error adding trigger word: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        buttonBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        loadTriggerWords();
    }

    private void loadTriggerWords() {
        triggerWordsController.getAllTriggerWords(words -> {
            triggerWordsList.clear();
            triggerWordsList.addAll(words);
            adapter.updateTriggerWords(triggerWordsList);
        }, e -> {
            Toast.makeText(getContext(), "Error loading trigger words: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}

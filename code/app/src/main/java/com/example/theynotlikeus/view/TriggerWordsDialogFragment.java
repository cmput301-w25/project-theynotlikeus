package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.adapter.TriggerWordsAdapter;
import com.example.theynotlikeus.controller.TriggerWordsController;
import java.util.ArrayList;
import java.util.List;

public class TriggerWordsDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private EditText editTextNewTrigger;
    private Button buttonAddTrigger;
    private TriggerWordsAdapter adapter;
    private List<String> triggerWordsList;
    private TriggerWordsController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trigger_words, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_trigger_words);
        editTextNewTrigger = view.findViewById(R.id.edittext_new_trigger);
        buttonAddTrigger = view.findViewById(R.id.button_add_trigger);

        triggerWordsList = new ArrayList<>();
        adapter = new TriggerWordsAdapter(triggerWordsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        controller = new TriggerWordsController();
        loadTriggerWords();

        buttonAddTrigger.setOnClickListener(v -> {
            String newWord = editTextNewTrigger.getText().toString().trim();
            if (TextUtils.isEmpty(newWord)) {
                Toast.makeText(getContext(), "Please enter a trigger word", Toast.LENGTH_SHORT).show();
                return;
            }
            controller.addTriggerWord(newWord, () -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        editTextNewTrigger.setText("");
                        loadTriggerWords();
                    });
                }
            }, e -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error adding trigger word: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });

        return view;
    }

    private void loadTriggerWords() {
        controller.getAllTriggerWords(words -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    triggerWordsList.clear();
                    triggerWordsList.addAll(words);
                    adapter.notifyDataSetChanged();
                });
            }
        }, e -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error loading trigger words: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}

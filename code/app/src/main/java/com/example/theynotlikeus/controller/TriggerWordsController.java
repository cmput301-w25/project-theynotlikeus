package com.example.theynotlikeus.controller;

import com.example.theynotlikeus.model.TriggerWord;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TriggerWordsController {
    private final FirebaseFirestore db;

    public TriggerWordsController() {
        // Assumes FirebaseFirestore is configured once in your Application class.
        this.db = FirebaseFirestore.getInstance();
    }

    // Fetch all trigger words as a list of Strings.
    public void getAllTriggerWords(Consumer<List<String>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("triggerWords")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> words = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String word = document.getString("word");
                        if (word != null) {
                            words.add(word);
                        }
                    }
                    onSuccess.accept(words);
                })
                .addOnFailureListener(onFailure::accept);
    }

    // Add a new trigger word.
    public void addTriggerWord(String word, Runnable onSuccess, Consumer<Exception> onFailure) {
        if (word == null || word.trim().isEmpty()) {
            onFailure.accept(new IllegalArgumentException("Word cannot be empty"));
            return;
        }
        db.collection("triggerWords")
                .add(new TriggerWord(word))
                .addOnSuccessListener(documentReference -> onSuccess.run())
                .addOnFailureListener(onFailure::accept);
    }
}

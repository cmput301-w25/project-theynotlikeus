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
        this.db = FirebaseFirestore.getInstance();
    }

    // Fetch all trigger words as a list of TriggerWord objects.
    public void getAllTriggerWords(Consumer<List<TriggerWord>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("triggerWords")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TriggerWord> words = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        TriggerWord triggerWord = document.toObject(TriggerWord.class);
                        triggerWord.setId(document.getId());
                        words.add(triggerWord);
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


    // Delete a trigger word by document ID.
    public void deleteTriggerWord(String documentId, Runnable onSuccess, Consumer<Exception> onFailure) {
        db.collection("triggerWords")
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(onFailure::accept);
    }

}

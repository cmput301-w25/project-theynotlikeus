package com.example.theynotlikeus.model;

public class TriggerWord {
    private String word;
    private String id;  // Added for deletion.

    // No-argument constructor required by Firestore.
    public TriggerWord() {}

    public TriggerWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    // Getter for the document ID.
    public String getId() {
        return id;
    }

    // Setter for the document ID.
    public void setId(String id) {
        this.id = id;
    }
}

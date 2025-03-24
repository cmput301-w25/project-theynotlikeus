package com.example.theynotlikeus.model;

public class TriggerWord {
    private String word;

    // No-argument constructor required for Firestore.
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
}

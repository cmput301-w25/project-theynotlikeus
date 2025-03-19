package com.example.theynotlikeus.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;

public class AddCommentsActivity extends AppCompatActivity {

    private String moodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comments);

        //moodID = getIntent().getStringExtra
    }
}

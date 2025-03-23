package com.example.theynotlikeus.singleton;

import android.app.Application;

public class MyApp extends Application {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

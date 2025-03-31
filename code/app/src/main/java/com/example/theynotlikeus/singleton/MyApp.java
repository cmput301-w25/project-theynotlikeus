package com.example.theynotlikeus.singleton;

import android.app.Application;
import com.example.theynotlikeus.notifications.NotificationHelper;

public class MyApp extends Application {
    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the notification channel so notifications can be sent.
        NotificationHelper.createNotificationChannel(this);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

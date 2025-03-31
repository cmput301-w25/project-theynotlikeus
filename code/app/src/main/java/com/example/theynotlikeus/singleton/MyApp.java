package com.example.theynotlikeus.singleton;

import android.app.Application;
import com.example.theynotlikeus.notifications.NotificationHelper;

public class MyApp extends Application {
    private static MyApp instance;
    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize the notification channel so notifications can be sent.
        NotificationHelper.createNotificationChannel(this);
    }
    // Static method to return the singleton instance.
    public static MyApp getInstance() {
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

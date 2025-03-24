package com.example.memorycardgame;

import android.app.Application;

public class MemoryGameApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved theme
        ThemeManager.applyTheme(this);
    }
}
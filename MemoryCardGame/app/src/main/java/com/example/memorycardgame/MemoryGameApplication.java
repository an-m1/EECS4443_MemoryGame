package com.example.memorycardgame;

import android.app.Application;

/**
 * MemoryGameApplication initializes global application state.
 * It applies the saved theme when the application is created.
 */
public class MemoryGameApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects have been created.
     * Applies the saved theme using the ThemeManager.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Apply the user's saved theme setting at app startup.
        ThemeManager.applyTheme(this);
    }
}

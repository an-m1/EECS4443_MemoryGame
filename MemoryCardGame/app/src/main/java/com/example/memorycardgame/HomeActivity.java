package com.example.memorycardgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * HomeActivity serves as the main entry point of the game,
 * providing navigation options for the user to start playing
 * or adjust game settings.
 */
public class HomeActivity extends AppCompatActivity {

    // UI elements for navigation
    private Button playButton;
    private Button settingsButton;

    /**
     * Initializes the HomeActivity by setting up the layout and attaching
     * click listeners to the navigation buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize navigation buttons
        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Set click listener for the "Play" button to start the game activity
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(gameIntent);
            }
        });

        // Set click listener for the "Settings" button to open the settings activity
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }
}

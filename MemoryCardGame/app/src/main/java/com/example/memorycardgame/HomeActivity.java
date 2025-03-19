package com.example.memorycardgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button playButton;
    private Button highScoresButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Set click listeners
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the game activity
                Intent gameIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(gameIntent);
            }
        });

        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast message
                // Later you can create a HighScoreActivity
                Toast.makeText(HomeActivity.this, "High Scores feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast message
                // Later you can create a SettingsActivity
                Toast.makeText(HomeActivity.this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
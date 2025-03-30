package com.example.memorycardgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to display the summary of a completed game.
 * It shows total time, moves, mismatches, efficiency, battery usage, and the selected theme.
 * Users can restart the game or return to the home screen.
 */
public class GameSummaryActivity extends AppCompatActivity {

    /**
     * Initializes the activity by setting up the UI and populating it with game summary data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);

        // Retrieve game data from Intent extras
        Intent intent = getIntent();
        int minutes = intent.getIntExtra("TOTAL_TIME_MINUTES", 0);
        int seconds = intent.getIntExtra("TOTAL_TIME_SECONDS", 0);
        int totalMoves = intent.getIntExtra("TOTAL_MOVES", 0);
        int mismatches = intent.getIntExtra("TOTAL_MISMATCHES", 0);
        int initialBatteryLevel = intent.getIntExtra("INITIAL_BATTERY_LEVEL", 0);
        int finalBatteryLevel = intent.getIntExtra("FINAL_BATTERY_LEVEL", 0);
        String currentTheme = intent.getStringExtra("CURRENT_THEME");

        // Initialize UI elements
        TextView timeView = findViewById(R.id.summaryTimeTextView);
        TextView movesView = findViewById(R.id.summaryMovesTextView);
        TextView mismatchesView = findViewById(R.id.summaryMismatchesTextView);
        TextView themeView = findViewById(R.id.summaryThemeTextView);
        TextView efficiencyView = findViewById(R.id.summaryEfficiencyTextView);
        TextView initialBatteryView = findViewById(R.id.summaryInitialBatteryTextView);
        TextView finalBatteryView = findViewById(R.id.summaryFinalBatteryTextView);
        TextView batteryDifferenceView = findViewById(R.id.summaryBatteryDifferenceTextView);

        // Calculate efficiency (percentage of pairs per move)
        // Here, 6.0 represents the total number of pairs in the game.
        double efficiency = (totalMoves > 0) ? (6.0 / totalMoves) * 100 : 0;

        // Calculate battery usage difference
        int batteryDifference = initialBatteryLevel - finalBatteryLevel;

        // Populate the TextViews with the game summary data
        timeView.setText(String.format("Total Time: %02d:%02d", minutes, seconds));
        movesView.setText(String.format("Total Moves: %d", totalMoves));
        mismatchesView.setText(String.format("Mismatches: %d", mismatches));
        efficiencyView.setText(String.format("Efficiency: %.2f%%", efficiency));
        initialBatteryView.setText(String.format("Initial Battery (%%): %d%%", initialBatteryLevel));
        finalBatteryView.setText(String.format("Final Battery (%%): %d%%", finalBatteryLevel));
        batteryDifferenceView.setText(String.format("Battery Used (%%): %d%%", batteryDifference));

        themeView.setText(String.format("Theme: %s", currentTheme));

        // Setup buttons to restart the game or return to the home screen
        Button restartButton = findViewById(R.id.summaryRestartButton);
        Button homeButton = findViewById(R.id.summaryHomeButton);

        // Restart game by launching MainActivity
        restartButton.setOnClickListener(v -> {
            Intent restartIntent = new Intent(this, MainActivity.class);
            startActivity(restartIntent);
            finish();
        });

        // Return to home screen by launching HomeActivity
        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}

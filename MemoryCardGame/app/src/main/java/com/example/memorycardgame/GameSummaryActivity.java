package com.example.memorycardgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameSummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);

        // Get intent extras
        Intent intent = getIntent();
        int minutes = intent.getIntExtra("TOTAL_TIME_MINUTES", 0);
        int seconds = intent.getIntExtra("TOTAL_TIME_SECONDS", 0);
        int totalMoves = intent.getIntExtra("TOTAL_MOVES", 0);
        int mismatches = intent.getIntExtra("TOTAL_MISMATCHES", 0);
        int batteryLevel = intent.getIntExtra("BATTERY_LEVEL", 0);
        String currentTheme = intent.getStringExtra("CURRENT_THEME");

        // Find TextViews
        TextView timeView = findViewById(R.id.summaryTimeTextView);
        TextView movesView = findViewById(R.id.summaryMovesTextView);
        TextView mismatchesView = findViewById(R.id.summaryMismatchesTextView);
        TextView themeView = findViewById(R.id.summaryThemeTextView);
        TextView efficiencyView = findViewById(R.id.summaryEfficiencyTextView);
        TextView batteryView = findViewById(R.id.summaryBatteryTextView);

        // Calculate efficiency (pairs per move)
        // 6.0 (total number of pairs in the game) divided by total moves
        double efficiency = (totalMoves > 0) ? (6.0 / totalMoves) * 100 : 0;

        // Set TextViews
        timeView.setText(String.format("Total Time: %02d:%02d", minutes, seconds));
        movesView.setText(String.format("Total Moves: %d", totalMoves));
        mismatchesView.setText(String.format("Mismatches: %d", mismatches));
        efficiencyView.setText(String.format("Efficiency: %.2f%%", efficiency));
        batteryView.setText(String.format("Battery Level: %d%%", batteryLevel));
        themeView.setText(String.format("Theme: %s", currentTheme));

        // Restart and Home buttons
        Button restartButton = findViewById(R.id.summaryRestartButton);
        Button homeButton = findViewById(R.id.summaryHomeButton);

        restartButton.setOnClickListener(v -> {
            Intent restartIntent = new Intent(this, MainActivity.class);
            startActivity(restartIntent);
            finish();
        });

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}
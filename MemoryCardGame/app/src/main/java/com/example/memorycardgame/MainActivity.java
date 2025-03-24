package com.example.memorycardgame;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView timeTextView;
    private TextView movesTextView;
    private ArrayList<String> emojis;
    private ArrayList<MemoryCard> cards;
    private MemoryCard firstCard;
    private MemoryCard secondCard;
    private boolean isProcessing = false;
    private int moves = 0;
    private int seconds = 0;
    private int minutes = 0;
    private Timer timer;
    private int remainingPairs;
    private int mismatches = 0; // Track number of mismatches

    // Variables for pause functionality
    private boolean isPaused = false;
    private FrameLayout pauseMenuContainer;
    private ImageButton optionsButton;
    private Button resumeButton;
    private Button restartButton;
    private Button settingsButton;
    private Button mainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Override device auto-brightness, for consistent brightness in each test.
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1.0f; // maximum brightness
        getWindow().setAttributes(layoutParams);

        gridLayout = findViewById(R.id.gridLayout);
        timeTextView = findViewById(R.id.timeTextView);
        movesTextView = findViewById(R.id.movesTextView);

        // Initialize pause menu components
        pauseMenuContainer = findViewById(R.id.pauseMenuContainer);
        optionsButton = findViewById(R.id.optionsButton);
        resumeButton = findViewById(R.id.resumeButton);
        restartButton = findViewById(R.id.restartButton);
        settingsButton = findViewById(R.id.gameSettingsButton);
        mainMenuButton = findViewById(R.id.mainMenuButton);

        // Set click listeners for pause menu buttons
        optionsButton.setOnClickListener(v -> pauseGame());
        resumeButton.setOnClickListener(v -> resumeGame());
        restartButton.setOnClickListener(v -> restartGame());
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        mainMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Initialize the game with a 4x6 grid (adjust rows/columns as needed)
        initializeGame(6, 4);
    }

    private void pauseGame() {
        if (!isPaused) {
            isPaused = true;

            // Pause the timer
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // Show the pause menu
            pauseMenuContainer.setVisibility(View.VISIBLE);
        }
    }

    private void resumeGame() {
        if (isPaused) {
            isPaused = false;

            // Hide the pause menu
            pauseMenuContainer.setVisibility(View.GONE);

            // Restart the timer if game is not complete
            if (remainingPairs > 0) {
                startTimer();
            }
        }
    }

    private void restartGame() {
        // Hide the pause menu
        pauseMenuContainer.setVisibility(View.GONE);
        isPaused = false;

        // Reinitialize the game
        initializeGame(6, 4);
    }

    private void initializeGame(int rows, int cols) {
        // Reset game state
        if (timer != null) timer.cancel();
        moves = 0;
        seconds = 0;
        minutes = 0;
        mismatches = 0;
        remainingPairs = (rows * cols) / 2;
        updateUI();

        // Initialize emojis
        emojis = new ArrayList<>();
        // emojis for 4x6 grid
        String[] emojiArray = {"🌹", "🌻", "🌺", "🌈", "🍓", "🍎", "🍉", "🍊",
                "🥭", "🍍", "🍋", "🍏", "🍐", "🥝", "🍇", "🥥",
                "🍅", "🌶️", "🍄", "🧅", "🥦", "🥑", "🍔", "🍕"};

        // Add pairs of emojis
        for (int i = 0; i < remainingPairs; i++) {
            emojis.add(emojiArray[i]);
            emojis.add(emojiArray[i]);
        }
        Collections.shuffle(emojis);

        // Setup grid layout
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);
        gridLayout.setRowCount(rows);
        gridLayout.setUseDefaultMargins(false);
        gridLayout.setPadding(4, 4, 4, 4);

        // Calculate the width of each card to make them square
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int totalMargin = (cols - 1) * 8;
        int gridPadding = 32;
        int cardWidth = (screenWidth - totalMargin - gridPadding) / cols;

        // Create cards
        cards = new ArrayList<>();
        for (int i = 0; i < rows * cols; i++) {
            MemoryCard card = new MemoryCard(this, emojis.get(i));
            card.setOnClickListener(v -> onCardClick((MemoryCard) v));

            // Fixed square dimensions
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.height = cardWidth;
            params.setMargins(4, 4, 4, 4);

            // Calculate row and column for this card
            int row = i / cols;
            int col = i % cols;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);

            card.setLayoutParams(params);
            cards.add(card);
            gridLayout.addView(card);
        }

        // Start timer
        startTimer();
    }

    private void onCardClick(MemoryCard card) {
        if (isProcessing || card.isFlipped() || isPaused) return;

        card.flip();

        if (firstCard == null) {
            firstCard = card;
        } else {
            secondCard = card;
            isProcessing = true;
            moves++;
            updateUI();

            new Handler().postDelayed(() -> {
                if (firstCard.matches(secondCard)) {
                    remainingPairs--;
                    if (remainingPairs == 0) {
                        gameWon();
                    }
                } else {
                    firstCard.flip();
                    secondCard.flip();
                    mismatches++; // Increment mismatches when cards don't match
                }
                firstCard = null;
                secondCard = null;
                isProcessing = false;
            }, 1000);
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    seconds++;
                    if (seconds == 60) {
                        minutes++;
                        seconds = 0;
                    }
                    updateUI();
                });
            }
        }, 1000, 1000);
    }

    private void updateUI() {
        String timeStr = String.format("Time: %02d:%02d", minutes, seconds);
        timeTextView.setText(timeStr);
        movesTextView.setText("Moves: " + moves);
    }

    private void gameWon() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        String message = String.format("Congratulations!\nCompleted in %d moves\nTime: %02d:%02d",
                moves, minutes, seconds);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Get battery level
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        // Launch GameSummaryActivity with game stats
        Intent summaryIntent = new Intent(this, GameSummaryActivity.class);
        summaryIntent.putExtra("TOTAL_TIME_MINUTES", minutes);
        summaryIntent.putExtra("TOTAL_TIME_SECONDS", seconds);
        summaryIntent.putExtra("TOTAL_MOVES", moves);
        summaryIntent.putExtra("TOTAL_MISMATCHES", mismatches);
        summaryIntent.putExtra("BATTERY_LEVEL", batteryLevel);

        // Determine current theme (implement dynamic tracking as needed)
        String currentTheme = "Light Mode";
        summaryIntent.putExtra("CURRENT_THEME", currentTheme);

        startActivity(summaryIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game if app goes to background
        if (!isPaused && timer != null) {
            pauseGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel timer to prevent memory leaks
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

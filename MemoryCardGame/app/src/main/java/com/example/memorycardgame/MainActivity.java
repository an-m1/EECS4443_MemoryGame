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

/**
 * MainActivity handles the gameplay of the Memory Card Game.
 * It sets up the game board, manages the timer and moves, handles user interactions with cards,
 * and provides pause/resume functionality.
 */
public class MainActivity extends AppCompatActivity {
    // UI elements for the game grid and game information
    private GridLayout gridLayout;
    private TextView timeTextView;
    private TextView movesTextView;

    // Game data structures
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
    private int initialBatteryLevel;

    // Variables for pause functionality
    private boolean isPaused = false;
    private FrameLayout pauseMenuContainer;
    private ImageButton optionsButton;
    private Button resumeButton;
    private Button restartButton;
    private Button settingsButton;
    private Button mainMenuButton;

    /**
     * Called when the activity is first created.
     * Initializes the game board, sets up UI components and pause menu, and starts the game.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set screen brightness to maximum for consistent gameplay lighting
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1.0f;
        getWindow().setAttributes(layoutParams);

        // Capture initial battery level when game starts
        initialBatteryLevel = getBatteryLevel();

        // Initialize game UI components
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

        // Set click listener for options button to pause the game
        optionsButton.setOnClickListener(v -> pauseGame());

        // Set click listener for resume button to resume the game
        resumeButton.setOnClickListener(v -> resumeGame());

        // Set click listener for restart button to restart the game
        restartButton.setOnClickListener(v -> restartGame());

        // Navigate to settings activity when settings button is clicked
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Navigate to home activity when main menu button is clicked
        mainMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Initialize the game board with a 6x4 grid
        initializeGame(6, 4);
    }

    /**
     * Pauses the game by cancelling the timer and displaying the pause menu.
     */
    private void pauseGame() {
        if (!isPaused) {
            isPaused = true;

            // Stop the timer to pause game progress
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // Display the pause menu
            pauseMenuContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Resumes the game by hiding the pause menu and restarting the timer.
     */
    private void resumeGame() {
        if (isPaused) {
            isPaused = false;

            // Hide the pause menu
            pauseMenuContainer.setVisibility(View.GONE);

            // Restart the timer if the game is still in progress
            if (remainingPairs > 0) {
                startTimer();
            }
        }
    }

    /**
     * Restarts the game by hiding the pause menu and reinitializing the game board.
     */
    private void restartGame() {
        pauseMenuContainer.setVisibility(View.GONE);
        isPaused = false;
        initializeGame(6, 4);
    }

    /**
     * Initializes the game board by resetting game state, shuffling emojis, and creating MemoryCard views.
     *
     * @param rows The number of rows for the grid layout.
     * @param cols The number of columns for the grid layout.
     */
    private void initializeGame(int rows, int cols) {
        // Reset game state and cancel any existing timer
        if (timer != null) timer.cancel();
        moves = 0;
        seconds = 0;
        minutes = 0;
        mismatches = 0;
        remainingPairs = (rows * cols) / 2;
        updateUI();

        // Initialize emojis and add pairs
        emojis = new ArrayList<>();
        String[] emojiArray = {"🌹", "🌻", "🌺", "🌈", "🍓", "🍎", "🍉", "🍊",
                "🥭", "🍍", "🍋", "🍏", "🍐", "🥝", "🍇", "🥥",
                "🍅", "🌶️", "🍄", "🧅", "🥦", "🥑", "🍔", "🍕"};
        for (int i = 0; i < remainingPairs; i++) {
            emojis.add(emojiArray[i]);
            emojis.add(emojiArray[i]);
        }
        Collections.shuffle(emojis);

        // Set up grid layout parameters
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);
        gridLayout.setRowCount(rows);
        gridLayout.setUseDefaultMargins(false);
        gridLayout.setPadding(4, 4, 4, 4);

        // Calculate card width to maintain square dimensions
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int totalMargin = (cols - 1) * 8;
        int gridPadding = 32;
        int cardWidth = (screenWidth - totalMargin - gridPadding) / cols;

        // Create and add MemoryCard views to the grid
        cards = new ArrayList<>();
        for (int i = 0; i < rows * cols; i++) {
            MemoryCard card = new MemoryCard(this, emojis.get(i));
            card.setOnClickListener(v -> onCardClick((MemoryCard) v));

            // Set layout parameters for square dimensions and margins
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.height = cardWidth;
            params.setMargins(4, 4, 4, 4);
            int row = i / cols;
            int col = i % cols;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);

            card.setLayoutParams(params);
            cards.add(card);
            gridLayout.addView(card);
        }

        // Start the game timer
        startTimer();
    }

    /**
     * Handles a click event on a MemoryCard.
     * Flips the card, checks for a match, updates moves and mismatches, and triggers game win when applicable.
     *
     * @param card The MemoryCard that was clicked.
     */
    private void onCardClick(MemoryCard card) {
        // Ignore clicks if already processing, card is flipped, or game is paused
        if (isProcessing || card.isFlipped() || isPaused) return;

        card.flip();

        if (firstCard == null) {
            // First card selected
            firstCard = card;
        } else {
            // Second card selected, process the match
            secondCard = card;
            isProcessing = true;
            moves++;
            updateUI();

            // Delay to allow user to see the flipped card
            new Handler().postDelayed(() -> {
                if (firstCard.matches(secondCard)) {
                    // Cards match; decrease remaining pairs count
                    remainingPairs--;
                    if (remainingPairs == 0) {
                        gameWon();
                    }
                } else {
                    // Cards do not match; flip them back and record mismatch
                    firstCard.flip();
                    secondCard.flip();
                    mismatches++;
                }
                firstCard = null;
                secondCard = null;
                isProcessing = false;
            }, 1000);
        }
    }

    /**
     * Starts the game timer which updates the time every second.
     */
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

    /**
     * Updates the UI elements such as the timer and moves counter.
     */
    private void updateUI() {
        String timeStr = String.format("Time: %02d:%02d", minutes, seconds);
        timeTextView.setText(timeStr);
        movesTextView.setText("Moves: " + moves);
    }

    /**
     * Retrieves the current battery level using the system BatteryManager.
     *
     * @return The current battery level as a percentage.
     */
    private int getBatteryLevel() {
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * Called when the game is won.
     * Stops the timer, displays a congratulatory message, and launches the GameSummaryActivity with game statistics.
     */
    private void gameWon() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        String message = String.format("Congratulations!\nCompleted in %d moves\nTime: %02d:%02d", moves, minutes, seconds);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Capture final battery level
        int finalBatteryLevel = getBatteryLevel();

        // Prepare and launch the game summary activity with collected game stats
        Intent summaryIntent = new Intent(this, GameSummaryActivity.class);
        summaryIntent.putExtra("TOTAL_TIME_MINUTES", minutes);
        summaryIntent.putExtra("TOTAL_TIME_SECONDS", seconds);
        summaryIntent.putExtra("TOTAL_MOVES", moves);
        summaryIntent.putExtra("TOTAL_MISMATCHES", mismatches);
        summaryIntent.putExtra("INITIAL_BATTERY_LEVEL", initialBatteryLevel);
        summaryIntent.putExtra("FINAL_BATTERY_LEVEL", finalBatteryLevel);

        // Determine current theme using ThemeManager utility
        boolean isDark = ThemeManager.isDarkMode(this);
        String currentTheme = isDark ? "Dark Mode" : "Light Mode";
        summaryIntent.putExtra("CURRENT_THEME", currentTheme);

        startActivity(summaryIntent);
        finish();
    }

    /**
     * Called when the activity is paused (e.g., when the app goes into the background).
     * Pauses the game if not already paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (!isPaused && timer != null) {
            pauseGame();
        }
    }

    /**
     * Called when the activity is destroyed.
     * Ensures that the timer is cancelled to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

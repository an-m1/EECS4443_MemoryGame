package com.example.memorycardgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import android.view.WindowManager;



/* TODO: when the game is complete, direct the user to a summary activity, where the users game stat are displayed.
    we want fields like:
    - time taken
    - number of mismatches
    - current mode (light/dark mode)
    - battery % lost (compare initial and final battery %)

    TODO: Find a way to disabled the devices "night shift", "true-tone", and "auto-brightness" to avoid display color changes during gameplay, and to maximize consistency between devices.
 */
public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView timeTextView;
    private TextView movesTextView;
    private ArrayList<String> emojis;
    private ArrayList<com.example.memorycardgame.MemoryCard> cards;
    private com.example.memorycardgame.MemoryCard firstCard;
    private com.example.memorycardgame.MemoryCard secondCard;
    private boolean isProcessing = false;
    private int moves = 0;
    private int seconds = 0;
    private int minutes = 0;
    private Timer timer;
    private int remainingPairs;

    // New variables for pause functionality
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

        initializeGame(6, 4); // Default 4x6 grid
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

        // Setup grid with square cells
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
            com.example.memorycardgame.MemoryCard card = new com.example.memorycardgame.MemoryCard(this, emojis.get(i));
            card.setOnClickListener(v -> onCardClick((com.example.memorycardgame.MemoryCard) v));

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

    private void onCardClick(com.example.memorycardgame.MemoryCard card) {
        // Don't allow card clicks when game is paused
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game when app goes to background
        if (!isPaused && timer != null) {
            pauseGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure to cancel timer to prevent memory leaks
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
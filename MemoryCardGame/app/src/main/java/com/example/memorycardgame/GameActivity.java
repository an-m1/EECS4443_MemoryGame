package com.example.memorycardgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private List<ImageButton> buttons;
    private List<MemoryCard> cards;
    private Integer indexOfSingleSelectedCard = null;

    private int secondsElapsed = 0;
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private Switch darkModeSwitch;
    private boolean isDarkTheme = false;
    private TextView timerTextView;
    private Button resetButton;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameFinished) {
                secondsElapsed++;
                updateTimer();
                timerHandler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadTheme(); // Load the saved theme
        darkModeSwitch = findViewById(R.id.themeSwitch);
        timerTextView = findViewById(R.id.timerTextView);
        resetButton = findViewById(R.id.resetButton);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.lightmodefacecard1);
        images.add(R.drawable.lightmodefacecard2);
        images.add(R.drawable.lightmodefacecard3);
        images.add(R.drawable.lightmodefacecard4);

        // Add each image twice so we can create pairs
        images.addAll(images);
        // Randomize the order of images
        Collections.shuffle(images);

        buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.imageButton1));
        buttons.add(findViewById(R.id.imageButton2));
        buttons.add(findViewById(R.id.imageButton3));
        buttons.add(findViewById(R.id.imageButton4));
        buttons.add(findViewById(R.id.imageButton5));
        buttons.add(findViewById(R.id.imageButton6));
        buttons.add(findViewById(R.id.imageButton7));
        buttons.add(findViewById(R.id.imageButton8));

        cards = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            cards.add(new MemoryCard(images.get(i)));
        }

        for (int i = 0; i < buttons.size(); i++) {
            final int index = i;
            buttons.get(i).setOnClickListener(v -> {
                if (!gameStarted) {
                    startGame(); // Start the timer when the first move is made
                }
                Log.i(TAG, "button clicked!!");
                // Update models
                updateModels(index);
                // Update the UI for the game
                updateViews();
            });
        }
        resetButton.setOnClickListener(v -> resetGame());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkTheme = isChecked;

            // Save the user's preference
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("dark_theme", isDarkTheme);
            editor.apply();
            resetGame();

            // Update the card images dynamically
            updateCardImages();
        });
    }

    private void resetGame() {
        gameFinished = false;
        gameStarted = false;
        secondsElapsed = 0;
        indexOfSingleSelectedCard = null;
        updateTimer();

        // Reshuffle images
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.lightmodefacecard1);
        images.add(R.drawable.lightmodefacecard2);
        images.add(R.drawable.lightmodefacecard3);
        images.add(R.drawable.lightmodefacecard4);
        images.addAll(images);
        Collections.shuffle(images);

        // Reset the cards and buttons
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setIdentifier(images.get(i));
            cards.get(i).setFaceUp(false);
            cards.get(i).setMatched(false);

            // Restore button opacity
            buttons.get(i).setAlpha(1.0f);
        }

        // Update UI
        updateViews();

        // Stop the timer if running
        timerHandler.removeCallbacks(timerRunnable);
    }


    private void startGame() {
        gameStarted = true;
        timerHandler.post(timerRunnable); // Start the timer
    }

    private void updateTimer() {
        timerTextView.setText("Time: " + secondsElapsed + "s");
    }

    private void updateViews() {
        int backImage = isDarkTheme ? R.drawable.darkmodeback : R.drawable.lightmodeback;
        for (int i = 0; i < cards.size(); i++) {
            MemoryCard card = cards.get(i);
            ImageButton button = buttons.get(i);
            if (card.isMatched()) {
                button.setAlpha(0.1f);
            }
            button.setImageResource(card.isFaceUp() ? card.getIdentifier() : backImage);
        }

        // Check if the game is finished
        if (allCardsMatched()) {
            finishGame();
        }
    }

    private boolean allCardsMatched() {
        for (MemoryCard card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

    private void finishGame() {
        if (!gameFinished) {
            gameFinished = true;
            timerHandler.removeCallbacks(timerRunnable); // Stop the timer
            Toast.makeText(this, "Congratulations! You finished the game in " + secondsElapsed + " seconds!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateModels(int position) {
        MemoryCard card = cards.get(position);
        // Error checking:
        if (card.isFaceUp()) {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Three cases
        // 0 cards previously flipped over => restore cards + flip over the selected card
        // 1 card previously flipped over => flip over the selected card + check if the images match
        // 2 cards previously flipped over => restore cards + flip over the selected card
        if (indexOfSingleSelectedCard == null) {
            // 0 or 2 selected cards previously
            restoreCards();
            indexOfSingleSelectedCard = position;
        } else {
            // exactly 1 card was selected previously
            checkForMatch(indexOfSingleSelectedCard, position);
            indexOfSingleSelectedCard = null;
        }
        card.setFaceUp(!card.isFaceUp());
    }

    private void restoreCards() {
        for (MemoryCard card : cards) {
            if (!card.isMatched()) {
                card.setFaceUp(false);
            }
        }
    }

    private void checkForMatch(int position1, int position2) {
        if (cards.get(position1).getIdentifier() == cards.get(position2).getIdentifier()) {
            Toast.makeText(this, "Match found!!", Toast.LENGTH_SHORT).show();
            cards.get(position1).setMatched(true);
            cards.get(position2).setMatched(true);
        }
    }

    // Function to save and apply the theme
    private void setThemeMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putInt("theme", mode);
        editor.apply();
        recreate(); // Restart activity to apply theme
    }

    // Function to load the saved theme
    private void loadTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        isDarkTheme = prefs.getBoolean("dark_theme", false); // Default to light mode
    }


    private void updateCardImages() {
        // Choose the correct set of images based on the theme
        int backImage = isDarkTheme ? R.drawable.darkmodeback : R.drawable.background;

        List<Integer> images = new ArrayList<>();
        images.add(isDarkTheme ? R.drawable.darkmodefacecard1 : R.drawable.lightmodefacecard1);
        images.add(isDarkTheme ? R.drawable.darkmodefacecard2 : R.drawable.lightmodefacecard2);
        images.add(isDarkTheme ? R.drawable.darkmodefacecard3 : R.drawable.lightmodefacecard3);
        images.add(isDarkTheme ? R.drawable.darkmodefacecard4 : R.drawable.lightmodefacecard4);

        images.addAll(images); // Create pairs
        Collections.shuffle(images);

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setIdentifier(images.get(i));
            buttons.get(i).setImageResource(cards.get(i).isFaceUp() ? images.get(i) : backImage);
        }
    }
}

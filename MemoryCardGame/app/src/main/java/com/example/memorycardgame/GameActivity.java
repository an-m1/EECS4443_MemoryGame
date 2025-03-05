package com.example.memorycardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<ImageButton> buttons;
    private List<MemoryCard> cards;
    private Integer indexOfSingleSelectedCard = null;

    private int secondsElapsed = 0;
    private boolean gameStarted = false;
    private boolean gameFinished = false;

    private TextView timerTextView;
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

        timerTextView = findViewById(R.id.timerTextView);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.facecard1);
        images.add(R.drawable.facecard2);
        images.add(R.drawable.facecard3);
        images.add(R.drawable.facecard4);

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
    }

    private void startGame() {
        gameStarted = true;
        timerHandler.post(timerRunnable); // Start the timer
    }

    private void updateTimer() {
        timerTextView.setText("Time: " + secondsElapsed + "s");
    }

    private void updateViews() {
        for (int i = 0; i < cards.size(); i++) {
            MemoryCard card = cards.get(i);
            ImageButton button = buttons.get(i);
            if (card.isMatched()) {
                button.setAlpha(0.1f);
            }
            button.setImageResource(card.isFaceUp() ? card.getIdentifier() : R.drawable.back);
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
}

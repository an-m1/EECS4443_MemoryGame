package com.example.memorycardgame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
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
    private ArrayList<com.example.memorycardgame.MemoryCard> cards;
    private com.example.memorycardgame.MemoryCard firstCard;
    private com.example.memorycardgame.MemoryCard secondCard;
    private boolean isProcessing = false;
    private int moves = 0;
    private int seconds = 0;
    private int minutes = 0;
    private Timer timer;
    private int remainingPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        timeTextView = findViewById(R.id.timeTextView);
        movesTextView = findViewById(R.id.movesTextView);

        initializeGame(4, 4); // Default 4x4 grid
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
        String[] emojiArray = {"🌹", "🌻", "🌺", "🌈", "🍓", "🍎", "🍉", "🍊",
                "🥭", "🍍", "🍋", "🍏", "🍐", "🥝", "🍇", "🥥"};

        // Add pairs of emojis
        for (int i = 0; i < remainingPairs; i++) {
            emojis.add(emojiArray[i]);
            emojis.add(emojiArray[i]);
        }
        Collections.shuffle(emojis);

        // Setup grid
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);
        gridLayout.setRowCount(rows);

        // Create cards
        cards = new ArrayList<>();
        for (int i = 0; i < rows * cols; i++) {
            com.example.memorycardgame.MemoryCard card = new com.example.memorycardgame.MemoryCard(this, emojis.get(i));
            card.setOnClickListener(v -> onCardClick((com.example.memorycardgame.MemoryCard) v));
            cards.add(card);
            gridLayout.addView(card);
        }

        // Start timer
        startTimer();
    }

    private void onCardClick(com.example.memorycardgame.MemoryCard card) {
        if (isProcessing || card.isFlipped()) return;

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
        timer.cancel();
        String message = String.format("Congratulations!\nCompleted in %d moves\nTime: %02d:%02d",
                moves, minutes, seconds);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

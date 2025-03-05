package com.example.memorycardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<ImageButton> buttons;
    private List<MemoryCard> cards;
    private Integer indexOfSingleSelectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
                Log.i(TAG, "button clicked!!");
                // Update models
                updateModels(index);
                // Update the UI for the game
                updateViews();
            });
        }
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

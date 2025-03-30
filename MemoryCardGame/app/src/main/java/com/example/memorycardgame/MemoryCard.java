package com.example.memorycardgame;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import androidx.cardview.widget.CardView;
import android.view.Gravity;
import android.os.Handler;

/**
 * MemoryCard represents an individual card in the memory matching game.
 * It displays a question mark on the front and an emoji on the back.
 * The card can be flipped with an animation, and compared with other MemoryCards for matching.
 */
public class MemoryCard extends CardView {

    // TextView for the front side (displays a question mark)
    private TextView frontView;
    // TextView for the back side (displays the emoji)
    private TextView backView;
    // Emoji to display on the back side
    private String emoji;
    // Indicates whether the card is currently flipped
    private boolean isFlipped = false;

    /**
     * Constructs a MemoryCard with the given emoji.
     *
     * @param context the application context
     * @param emoji   the emoji to display on the back side of the card
     */
    public MemoryCard(Context context, String emoji) {
        super(context);
        this.emoji = emoji;
        initialize(context);
    }

    /**
     * Initializes the MemoryCard by setting up its layout parameters and child views.
     *
     * @param context the application context
     */
    private void initialize(Context context) {
        // Configure CardView properties for elevation, rounded corners, and padding support
        setCardElevation(4);
        setRadius(8);
        setUseCompatPadding(true);

        // Set layout parameters for proper spacing in a grid layout
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 2, 2, 2);
        setLayoutParams(params);

        // Create and configure the front view that displays a question mark
        frontView = new TextView(context);
        frontView.setText("?");
        frontView.setTextSize(28);
        frontView.setGravity(Gravity.CENTER);
        frontView.setVisibility(View.VISIBLE);
        addView(frontView);

        // Create and configure the back view that displays the emoji
        backView = new TextView(context);
        backView.setText(emoji);
        backView.setTextSize(28);
        backView.setGravity(Gravity.CENTER);
        backView.setVisibility(View.GONE);
        addView(backView);
    }

    /**
     * Flips the card with an animation. If the card is face down, it flips to show the emoji,
     * and vice versa.
     */
    public void flip() {
        // Toggle the flipped state
        isFlipped = !isFlipped;
        // Animate rotation along the Y-axis based on the new flip state
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "rotationY", isFlipped ? 180f : 0f);
        anim.setDuration(300);
        anim.start();

        // Update the visibility of the front and back views after half the animation duration
        new Handler().postDelayed(() -> {
            frontView.setVisibility(isFlipped ? View.GONE : View.VISIBLE);
            backView.setVisibility(isFlipped ? View.VISIBLE : View.GONE);
        }, 150);
    }

    /**
     * Checks if the card is currently flipped.
     *
     * @return true if the card is flipped, false otherwise
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Compares this MemoryCard with another for a matching emoji.
     *
     * @param other the other MemoryCard to compare with
     * @return true if both cards display the same emoji, false otherwise
     */
    public boolean matches(MemoryCard other) {
        return this.emoji.equals(other.emoji);
    }
}

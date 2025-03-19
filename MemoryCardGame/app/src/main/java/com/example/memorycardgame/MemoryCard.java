package com.example.memorycardgame;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import androidx.cardview.widget.CardView;
import android.view.Gravity;

public class MemoryCard extends CardView {
    private TextView frontView;
    private TextView backView;
    private String emoji;
    private boolean isFlipped = false;

    public MemoryCard(Context context, String emoji) {
        super(context);
        this.emoji = emoji;
        initialize(context);
    }

    private void initialize(Context context) {
        // Card setup
        setCardElevation(4);
        setRadius(8);
        setUseCompatPadding(true);

        // Layout parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        setLayoutParams(params);

        // Front view (question mark)
        frontView = new TextView(context);
        frontView.setText("?");
        frontView.setTextSize(24);
        frontView.setGravity(Gravity.CENTER);
        frontView.setVisibility(View.VISIBLE);
        addView(frontView);

        // Back view (emoji)
        backView = new TextView(context);
        backView.setText(emoji);
        backView.setTextSize(24);
        backView.setGravity(Gravity.CENTER);
        backView.setVisibility(View.GONE);
        addView(backView);
    }

    public void flip() {
        isFlipped = !isFlipped;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "rotationY",
                isFlipped ? 180f : 0f);
        anim.setDuration(300);
        anim.start();

        new Handler().postDelayed(() -> {
            frontView.setVisibility(isFlipped ? View.GONE : View.VISIBLE);
            backView.setVisibility(isFlipped ? View.VISIBLE : View.GONE);
        }, 150);
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public boolean matches(MemoryCard other) {
        return this.emoji.equals(other.emoji);
    }
}

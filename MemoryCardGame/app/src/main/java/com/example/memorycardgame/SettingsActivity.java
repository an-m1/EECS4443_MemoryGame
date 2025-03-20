package com.example.memorycardgame;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private SeekBar volumeSeekBar;
    private Switch musicSwitch;
    private Switch darkModeSwitch;
    private Switch vibrationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI elements
        backButton = findViewById(R.id.backButton);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        musicSwitch = findViewById(R.id.musicSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);

        // Set up back button to close this activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up volume seek bar listener
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO
            }
        });

        // Set up switch listeners
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO
        });
    }
}
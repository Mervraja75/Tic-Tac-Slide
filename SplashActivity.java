package com.example.slide.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slide.MainActivity;
import com.example.slide.AboutActivity;
import com.example.slide.SettingsActivity;
import com.example.slide.R;

public class SplashActivity extends AppCompatActivity {

    private Button aboutButton, settingsButton, onePlayerButton, twoPlayerButton;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /** Initialize buttons
         *
         */
        aboutButton = findViewById(R.id.about_button);
        settingsButton = findViewById(R.id.settings_button);
        onePlayerButton = findViewById(R.id.one_player_button);
        twoPlayerButton = findViewById(R.id.two_player_button);

        /** Initialize background music
         *
         */
        backgroundMusic = MediaPlayer.create(this, R.raw.default_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        /** Set up button listeners
         *
         */
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, AboutActivity.class));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SplashActivity", "Settings button clicked!");
                Intent intent = new Intent(SplashActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        onePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("gameMode", "OnePlayer");
                startActivity(intent);
                finish(); // Close Splash screen
            }
        });

        twoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("gameMode", "TwoPlayer");
                startActivity(intent);
                finish(); // Close Splash screen
            }
        });

        /** Add a delay before enabling buttons
         *
         */
        new Handler().postDelayed(() -> {
            /** Enable buttons or any other logic after delay
             *
             */
        }, 3000); // 3000 milliseconds = 3 seconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** Resume music when activity returns to the foreground
         *
         */
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /** Pause music when activity goes to the background
         *
         */
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Release the MediaPlayer resources
         *
         */
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}

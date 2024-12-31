package com.example.slide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slide.logic.GameMode;
import com.example.slide.ui.CustomView;

import java.util.Locale;

/**
 * MainActivity is the entry point for the game. It handles game initialization,
 * theme selection, language settings, and background music playback.
 */
public class MainActivity extends AppCompatActivity {

    private CustomView gv; // The custom game view
    private MediaPlayer backgroundMusic; // MediaPlayer for background music
    private String currentTheme; // Tracks the currently applied theme
    private String currentLanguage; // Tracks the currently applied language

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create and set the updated CustomView
        CustomView customView = new CustomView(this);
        setContentView(customView);

        // Apply the selected language
        applyLanguage();

        // Get the selected theme from preferences
        SharedPreferences prefs = getSharedPreferences("com.example.slide_preferences", Context.MODE_PRIVATE);
        currentTheme = prefs.getString("theme_selector", "default_theme");

        // Initialize the CustomView
        gv = new CustomView(this);

        // Apply the selected theme to the CustomView
        applyThemeToGameView();

        // Play background music for the selected theme
        playThemeMusic();

        // Check the intent for the game mode and set it
        if (getIntent().hasExtra("gameMode")) {
            String mode = getIntent().getStringExtra("gameMode");
            if ("OnePlayer".equals(mode)) {
                gv.setGameMode(GameMode.ONE_PLAYER);
            }
        }

        // Set the content view
        setContentView(gv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Apply the updated language
        applyLanguage();

        // Get the updated theme from preferences
        SharedPreferences prefs = getSharedPreferences("com.example.slide_preferences", Context.MODE_PRIVATE);
        String selectedTheme = prefs.getString("theme_selector", "default_theme");

        // If the theme has changed, update the CustomView and background music
        if (!selectedTheme.equals(currentTheme)) {
            currentTheme = selectedTheme;
            applyThemeToGameView();
            playThemeMusic();
        }

        // Resume background music if it is paused
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause music when the activity goes to the background
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }

    /**
     * Apply the selected theme to the CustomView.
     */
    private void applyThemeToGameView() {
        gv.setTheme(currentTheme);
    }

    /**
     * Play background music based on the selected theme.
     */
    private void playThemeMusic() {
        // Stop and release any existing background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
        }

        // Determine the music resource for the selected theme
        int themeMusic = R.raw.default_music; // Default music
        if ("hawaiian_theme".equals(currentTheme)) {
            themeMusic = R.raw.hawaiian_music;
        } else if ("tongan_theme".equals(currentTheme)) {
            themeMusic = R.raw.tongan_music;
        }

        // Initialize and start the MediaPlayer with the selected theme's music
        backgroundMusic = MediaPlayer.create(this, themeMusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
    }

    /**
     * Apply the selected language from preferences.
     */
    private void applyLanguage() {
        // Get the selected language from preferences
        SharedPreferences prefs = getSharedPreferences("com.example.slide_preferences", Context.MODE_PRIVATE);
        String selectedLanguage = prefs.getString("language_selector", "en"); // Default to English

        if (!selectedLanguage.equals(currentLanguage)) {
            currentLanguage = selectedLanguage;
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }
}

package com.example.slide.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.slide.R;
import com.example.slide.logic.*;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends RelativeLayout implements TickListener {

    private Grid grid;
    private boolean firstRun;
    private GridButton[] buttons;
    private List<GuiToken> tokens;
    private GameBoard engine;
    private GameHandler tim;
    private GameMode gameMode;
    private DifficultyLevel difficultyLevel;
    private Button backButton;

    private String currentTheme;
    private int[] backgroundImages;
    private int currentBackgroundIndex = 0;

    public CustomView(Context context) {
        super(context);
        initialize(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * Initializes the game view, setting up default values and loading theme preferences.
     *
     * @param context The application context.
     */
    private void initialize(Context context) {
        firstRun = true;
        buttons = new GridButton[10];
        tokens = new ArrayList<>();
        engine = new GameBoard();
        tim = new GameHandler();
        tim.register(this);
        gameMode = GameMode.ONE_PLAYER;
        difficultyLevel = DifficultyLevel.EASY;

        // Load the initial theme from shared preferences
        SharedPreferences prefs = context.getSharedPreferences("com.example.slide_preferences", Context.MODE_PRIVATE);
        currentTheme = prefs.getString("theme_selector", "default_theme"); // Default to "default_theme"

        // Apply the loaded theme
        applyTheme(currentTheme);

        // Create and configure the back button
        createBackButton(context);
    }

    /**
     * Creates a back button and positions it on the top-left corner.
     */
    private void createBackButton(Context context) {
        backButton = new Button(context);
        backButton.setText("Back");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.setMargins(16, 16, 0, 0); // Add some margin

        backButton.setLayoutParams(params);
        backButton.setOnClickListener(v -> {
            ((Activity) context).finish(); // Close the game and return to home screen
        });

        this.addView(backButton); // Add the button to the RelativeLayout
    }

    /**
     * Applies the selected theme to update the background images.
     *
     * @param theme The selected theme name (e.g., "default_theme", "hawaiian_theme", "tongan_theme").
     */
    private void applyTheme(String theme) {
        switch (theme) {
            case "hawaiian_theme":
                backgroundImages = new int[]{
                        R.drawable.hawaiian1,
                        R.drawable.hawaiian2,
                        R.drawable.hawaiian3,
                        R.drawable.hawaiian4
                };
                break;

            case "tongan_theme":
                backgroundImages = new int[]{
                        R.drawable.tongan1,
                        R.drawable.tongan2,
                        R.drawable.tongan3,
                        R.drawable.tongan4
                };
                break;

            default: // Default theme
                backgroundImages = new int[]{
                        R.drawable.default1,
                        R.drawable.default2,
                        R.drawable.default3,
                        R.drawable.default4
                };
        }

        // Set the first background image for the selected theme
        if (backgroundImages != null && backgroundImages.length > 0) {
            setBackgroundResource(backgroundImages[0]);
            currentBackgroundIndex = 0;
        } else {
            Log.e("CustomView", "No background images available for theme: " + theme);
        }
    }

    /**
     * Updates the theme dynamically and refreshes the view.
     *
     * @param theme The selected theme name.
     */
    public void setTheme(String theme) {
        if (theme.equals(currentTheme)) {
            Log.d("CustomView", "Theme is already applied: " + theme);
            return;
        }

        currentTheme = theme;
        applyTheme(theme);
        invalidate(); // Refresh the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstRun) {
            init();
            firstRun = false;
        }

        grid.draw(canvas);

        for (GuiToken tok : tokens) {
            tok.draw(canvas);
        }

        for (GridButton b : buttons) {
            b.draw(canvas);
        }
    }

    private void init() {
        float w = getWidth();
        float unit = w / 16f;
        float gridX = unit * 2.5f;
        float cellSize = unit * 2.3f;
        float gridY = unit * 9;
        grid = new Grid(gridX, gridY, cellSize);

        float buttonTop = gridY - cellSize;
        float buttonLeft = gridX - cellSize;

        for (int i = 0; i < 5; i++) {
            buttons[i] = new GridButton((char) ('1' + i), this, buttonLeft + cellSize * (i + 1), buttonTop, cellSize);
        }

        for (int i = 0; i < 5; i++) {
            buttons[5 + i] = new GridButton((char) ('A' + i), this, buttonLeft, buttonTop + cellSize * (i + 1), cellSize);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {
        cleanupFallenTokens();
        if (m.getAction() == MotionEvent.ACTION_DOWN && !anyMovers()) {
            float x = m.getX();
            float y = m.getY();
            boolean missed = true;

            for (GridButton b : buttons) {
                if (b.contains(x, y)) {
                    b.press();

                    // Create a new GuiToken
                    GuiToken tok = new GuiToken(engine.getCurrentPlayer(), b, getResources(), currentTheme);
                    engine.submitMove(b.getLabel());
                    tokens.add(tok);
                    tim.register(tok);
                    setupAnimation(b, tok);
                    missed = false;
                }
            }

            if (missed) {
                Toast.makeText(getContext(), "Please touch a button", Toast.LENGTH_SHORT).show();
            }
        } else if (m.getAction() == MotionEvent.ACTION_UP) {
            for (GridButton b : buttons) {
                b.release();
            }
        }
        return true;
    }

    private boolean anyMovers() {
        return tokens.stream().anyMatch(GuiToken::isMoving);
    }

    private void cleanupFallenTokens() {
        tokens.removeIf(t -> {
            if (t.isInvisible(getHeight())) {
                tim.unregister(t);
                return true;
            }
            return false;
        });
    }

    private void setupAnimation(GridButton b, GuiToken tok) {
        List<GuiToken> neighbors = new ArrayList<>();
        neighbors.add(tok);

        if (b.isTopButton()) {
            char col = b.getLabel();
            for (char row = 'A'; row <= 'E'; row++) {
                GuiToken other = findTokenAt(row, col);
                if (other != null) {
                    neighbors.add(other);
                } else {
                    break;
                }
            }
            neighbors.forEach(GuiToken::startMovingDown);
        } else {
            char row = b.getLabel();
            for (char col = '1'; col <= '5'; col++) {
                GuiToken other = findTokenAt(row, col);
                if (other != null) {
                    neighbors.add(other);
                } else {
                    break;
                }
            }
            neighbors.forEach(GuiToken::startMovingRight);
        }
    }

    private GuiToken findTokenAt(char row, char col) {
        return tokens.stream().filter(t -> t.matches(row, col)).findFirst().orElse(null);
    }

    @Override
    public void onTick() {
        if (!GuiToken.anyMovers()) {
            if (tokens.isEmpty()) return;

            Player winner = engine.checkForWin();
            if (winner != Player.BLANK) {
                tim.pause();
                showGameOverDialog(winner);
                return;
            }

            if (isTie()) {
                tim.pause();
                showGameOverDialog(Player.BLANK);
                return;
            }
        }
        invalidate();
    }

    private void showGameOverDialog(Player winner) {
        // Game over dialog logic
    }

    private boolean isTie() {
        for (GridButton b : buttons) {
            if (!b.isPressed()) {
                return false;
            }
        }
        return engine.checkForWin() == Player.BLANK;
    }

    public void setGameMode(GameMode gameMode) {

    }
}

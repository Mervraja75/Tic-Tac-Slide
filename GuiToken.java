package com.example.slide.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.example.slide.logic.TickListener;
import com.example.slide.logic.Player;

import com.example.slide.R;

/**
 * Represents a single X or O on the grid.
 * It is the graphical analog to the Player enum.
 */
public class GuiToken implements TickListener {
    private Player player;
    private RectF bounds;
    private PointF velocity;
    private Bitmap image;
    private GridPosition gp;
    private static int movers = 0;
    private int stepCounter;
    private final int STEPS = 11;
    private boolean falling;

    public class GridPosition {
        public char row;
        public char col;
    }

    /**
     * Create a new GuiToken object
     *
     * @param p      The Player (X or O) who created the token
     * @param parent which button was tapped to create the token
     * @param res    the Resources object (used for loading image)
     */
    public GuiToken(Player p, GridButton parent, Resources res, String theme) {
        gp = new GridPosition();
        if (parent.isTopButton()) {
            gp.row = 'A' - 1;
            gp.col = parent.getLabel();
        } else {
            gp.row = parent.getLabel();
            gp.col = '1' - 1;
        }

        if (theme == null) {
            theme = "default_theme";
        }

        this.bounds = new RectF(parent.getBounds());
        velocity = new PointF();
        falling = false;
        player = p;

        int imageResource = 0;

        // Set the correct image resource based on theme and player
        if ("hawaiian_theme".equals(theme)) {
            imageResource = (player == Player.X) ? R.drawable.hawaiian_x_token : R.drawable.hawaiian_o_token;
        } else if ("tongan_theme".equals(theme)) {
            imageResource = (player == Player.X) ? R.drawable.tongan_x_token : R.drawable.tongan_o_token;
        } else { // Default theme
            imageResource = (player == Player.X) ? R.drawable.player_x : R.drawable.player_o;
        }

        Log.d("GuiToken", "Theme: " + theme + ", Player: " + player + ", Resource: " + imageResource);

        try {
            image = BitmapFactory.decodeResource(res, imageResource);
            if (image == null) {
                throw new IllegalStateException("Failed to load Bitmap for resource ID: " + imageResource);
            }

            // Scale the image to fit the token bounds
            if (bounds.width() > 0 && bounds.height() > 0) {
                image = Bitmap.createScaledBitmap(image, (int) bounds.width(), (int) bounds.height(), true);
            } else {
                throw new IllegalStateException("Invalid bounds: width=" + bounds.width() + ", height=" + bounds.height());
            }
        } catch (Exception e) {
            Log.e("GuiToken", "Error loading Bitmap for Player: " + player + ", Theme: " + theme, e);

            // Fallback to colored rectangle
            image = Bitmap.createBitmap((int) bounds.width(), (int) bounds.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            Paint paint = new Paint();
            paint.setColor(player == Player.X ? Color.RED : Color.BLUE);
            canvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);
            Log.e("GuiToken", "Using fallback colored rectangle for Player: " + player);
        }
    }








    /**
     * Draw the token at the correct location, using the correct
     * image (X or O)
     * @param c The Canvas object supplied by onDraw
     */
    public void draw(Canvas c) {
        c.drawBitmap(image, bounds.left, bounds.top, new Paint());
    }

    /**
     * Move the token by its current velocity.
     * Stop when it reaches its destination location.
     */
    public void move() {
        if (falling) {
            velocity.y *= 2;
        } else {
            if (velocity.x != 0 || velocity.y != 0) {
                if (stepCounter >= STEPS) {
                    velocity.set(0, 0);
                    movers--;
                    if (fellOff()) {
                        velocity.set(0, 1);
                        falling = true;
                    }
                } else {
                    stepCounter++;
                }
            }
        }
        bounds.offset(velocity.x, velocity.y);
    }

    private boolean fellOff() {
        return (gp.col > '5' || gp.row > 'E');
    }

    public boolean isInvisible(int h) {
        return (bounds.top > h);
    }

    /**
     * Helper method for tokens created by the top row of buttons
     */
    public void startMovingDown() {
        startMoving(0, bounds.width() / STEPS);
        gp.row++;
    }

    /**
     * Helper method for tokens created by the left column of buttons
     */
    public void startMovingRight() {
        startMoving(bounds.width() / STEPS, 0);
        gp.col++;
    }

    private void startMoving(float vx, float vy) {
        velocity.set(vx, vy);
        movers++;
        stepCounter = 0;
    }

    /**
     * Is animation currently happening?
     * @return true if the token is currently moving (i.e. has a non-zero velocity); false otherwise.
     */
    public boolean isMoving() {
        return (velocity.x > 0 || velocity.y > 0);
    }

    public static boolean anyMovers() {
        return movers > 0;
    }

    @Override
    public void onTick() {
        move();
    }

    public boolean matches(char row, char col) {
        return (gp.row == row && gp.col == col);
    }
}

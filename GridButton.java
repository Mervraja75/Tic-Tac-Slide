package com.example.slide.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import com.example.slide.R;

public class GridButton {

    private RectF bounds;
    private Bitmap unpressedButton, pressedButton;;
    private char label;
    private boolean pressed;

    public GridButton(char name, View parent, float x, float y, float width) {
        label = name;
        bounds = new RectF(x, y, x+width, y+width);
        unpressedButton = BitmapFactory.decodeResource(parent.getResources(), R.drawable.unpressed_button);
        unpressedButton = Bitmap.createScaledBitmap(unpressedButton, (int)width, (int)width, true);
        pressedButton = BitmapFactory.decodeResource(parent.getResources(), R.drawable.pressed_button);
        pressedButton = Bitmap.createScaledBitmap(pressedButton, (int)width, (int)width, true);
        pressed = false;

    }

    /**
     * Draw the button at its correct place on the screen
     * @param c the Canvas object to draw to
     */
    public void draw(Canvas c) {
        if (pressed) {
            c.drawBitmap(pressedButton, bounds.left, bounds.top, null);
        } else {
            c.drawBitmap(unpressedButton, bounds.left, bounds.top, null);
        }
    }

    /**
     * Tests whether or not a given (x,y) point is inside the button or not
     * @param x the point to test against the bounds
     * @param y the point to test against the bounds
     * @return true if (x,y) is inside the bounds; else false
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x,y);
    }

    /**
     * Change the button to a "pressed" state
     */
    public void press() {
        pressed = true;
    }

    /**
     * Change the button to an "unpressed" state
     */
    public void release() {
        pressed = false;
    }

    /**
     * Simple "getter" method for the button's label
     * @return
     */
    public char getLabel() {
        return label;
    }

    /**
     * Is this button on the top row or the left column?
     * @return true if top, false if side.
     */
    public boolean isTopButton() {
        return (label >= '1' && label <= '5');
    }

    /**
     * Is this button on the top row or the left column?
     * @return true if side, false if top.
     */
    public boolean isLeftButton() {
        return (label >= 'A' && label <= 'E');
    }

    /**
     * Get the current dimensions of the button
     * @return a RectF object
     */
    public RectF getBounds() {
        return bounds;
    }

    public boolean isAvailable() {
        return false;
    }

    public void performClick() {
    }

    public boolean isPressed() {
        return pressed; // Correctly return the state of the button
    }

    public char getRow() {
        return isLeftButton() ? label : (char) ('A' + (label - '1'));
    }

    public char getCol() {
        return isTopButton() ? label : (char) ('1' + (label - 'A'));
    }

    public void reset() {

    }
}
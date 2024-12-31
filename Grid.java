package com.example.slide.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * This class is responsible for drawing a 5x5 grid on the screen.
 */
public class Grid {

    private final int dim = 5;
    private float lineWidth;
    private Paint paint;
    private RectF bounds;
    private float cellWidth;

    /**
     * initializes the grid
     * @param x the leftmost x coordinate
     * @param y the topmost y coordinate
     * @param cellWidth how wide each cell should be
     */
    public Grid(float x, float y, float cellWidth) {
        this.cellWidth = cellWidth;
        lineWidth = cellWidth/20;
        bounds = new RectF(x, y, x+cellWidth*dim, y+cellWidth*dim);
        paint = new Paint();
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.BLACK);
        paint.setStyle(Style.STROKE);
    }

    /**
     * check if the user tapped anywhere inside the grid
     * @param x the x position of the user's finger
     * @param y the y position of the user's finger
     * @return true if tapped inside; otherwise false
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    /**
     * simple "getter" that returns the topmost y coordinate of the grid
     * @return
     */
    public float getTop() {
        return bounds.top;
    }

    /**
     * draw the grid onto the screen
     * @param c the Canvas object, provided by the View
     */
    public void draw(Canvas c) {
        for (int i=0; i<=dim; i++) {
            c.drawLine(bounds.left, bounds.top + cellWidth*i, bounds.right, bounds.top + cellWidth*i, paint);
        }
        for (int i=0; i<=dim; i++) {
            c.drawLine(bounds.left + cellWidth*i, bounds.top, bounds.left + cellWidth*i, bounds.bottom, paint);
        }
    }

}
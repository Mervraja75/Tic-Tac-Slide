package com.example.slide.logic;

/**
 * This enum defines the possible states for each cell on the game board.
 * Each cell can contain an X, an O, be empty (BLANK), or indicate a tie.
 */
public enum Player {
    X,    // Represents player X
    O,    // Represents player O
    BLANK, // Represents an empty cell
    TIE    // Used to represent a tie outcome
}

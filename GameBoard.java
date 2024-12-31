package com.example.slide.logic;

import android.util.Log;
import java.util.logging.Logger;

public class GameBoard {
    private Player[][] grid;
    private final int DIM = 5;
    private Player currentPlayer;
    private static final Logger logger = Logger.getLogger(GameBoard.class.getName());

    // Scoring system for strategic positions
    private static final int[][] POSITION_SCORES = {
            {3, 2, 3, 2, 3}, // Row A
            {2, 4, 4, 4, 2}, // Row B
            {3, 4, 5, 4, 3}, // Row C (center has the highest score)
            {2, 4, 4, 4, 2}, // Row D
            {3, 2, 3, 2, 3}  // Row E
    };

    public GameBoard() {
        grid = new Player[DIM][DIM];
        clear();
        currentPlayer = Player.X;
    }

    /** Resets the game board to its initial state. */
    public void clear() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                grid[i][j] = Player.BLANK;
            }
        }
    }

    /** Processes a move by sliding tokens in the chosen row or column.
     * @param move The character '1'-'5' for vertical moves or 'A'-'E' for horizontal moves.
     * @return true if the move was successful, false otherwise.
     */
    public boolean submitMove(char move) {
        Log.d("GameBoard", "Submitting move: " + move);
        boolean moveSuccessful = false;

        if (move >= '1' && move <= '5') {
            int col = move - '1';
            moveSuccessful = slideColumn(col);
        } else if (move >= 'A' && move <= 'E') {
            int row = move - 'A';
            moveSuccessful = slideRow(row);
        }

        if (moveSuccessful) {
            togglePlayer();
        } else {
            Log.d("GameBoard", "Invalid move: " + move);
        }

        return moveSuccessful;
    }

    /** Slides tokens in the specified column downwards. */
    private boolean slideColumn(int col) {
        Player newVal = currentPlayer;
        for (int i = 0; i < DIM; i++) {
            if (grid[i][col] == Player.BLANK) {
                grid[i][col] = newVal;
                Log.d("GameBoard", "Move accepted at [" + i + "][" + col + "]");
                return true;
            } else {
                Player temp = grid[i][col];
                grid[i][col] = newVal;
                newVal = temp;
            }
        }
        return false;
    }

    /** Slides tokens in the specified row to the right. */
    private boolean slideRow(int row) {
        Player newVal = currentPlayer;
        for (int i = 0; i < DIM; i++) {
            if (grid[row][i] == Player.BLANK) {
                grid[row][i] = newVal;
                Log.d("GameBoard", "Move accepted at [" + row + "][" + i + "]");
                return true;
            } else {
                Player temp = grid[row][i];
                grid[row][i] = newVal;
                newVal = temp;
            }
        }
        return false;
    }

    /** Toggles the current player between X and O. */
    private void togglePlayer() {
        currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
    }

    /** Returns the score of a specific grid position based on its strategic importance.
     * @param row The row character ('A' to 'E').
     * @param col The column character ('1' to '5').
     * @return The score of the position.
     */
    public int getPositionScore(char row, char col) {
        int rowIndex = row - 'A'; // Convert row character to index (e.g., 'A' -> 0, 'B' -> 1, etc.)
        int colIndex = col - '1'; // Convert column character to index (e.g., '1' -> 0, '2' -> 1, etc.)
        return POSITION_SCORES[rowIndex][colIndex];
    }

    /** Simulates placing a move temporarily for a specific player. */
    public void simulateMove(char move, Player player) {
        if (move >= '1' && move <= '5') {
            int col = move - '1';
            simulateColumn(col, player);
        } else if (move >= 'A' && move <= 'E') {
            int row = move - 'A';
            simulateRow(row, player);
        }
    }

    private void simulateColumn(int col, Player player) {
        for (int i = 0; i < DIM; i++) {
            if (grid[i][col] == Player.BLANK) {
                grid[i][col] = player;
                break;
            }
        }
    }

    private void simulateRow(int row, Player player) {
        for (int i = 0; i < DIM; i++) {
            if (grid[row][i] == Player.BLANK) {
                grid[row][i] = player;
                break;
            }
        }
    }

    /** Undoes a simulated move by clearing the last move in the specified row or column. */
    public void undoMove(char move) {
        if (move >= '1' && move <= '5') {
            int col = move - '1';
            undoColumn(col);
        } else if (move >= 'A' && move <= 'E') {
            int row = move - 'A';
            undoRow(row);
        }
    }

    private void undoColumn(int col) {
        for (int i = DIM - 1; i >= 0; i--) {
            if (grid[i][col] != Player.BLANK) {
                grid[i][col] = Player.BLANK;
                break;
            }
        }
    }

    private void undoRow(int row) {
        for (int i = DIM - 1; i >= 0; i--) {
            if (grid[row][i] != Player.BLANK) {
                grid[row][i] = Player.BLANK;
                break;
            }
        }
    }

    /** Checks for a winning sequence in rows, columns, and diagonals. */
    public Player checkForWin() {
        Player winner = checkRows();
        if (winner != Player.BLANK) return winner;
        winner = checkColumns();
        if (winner != Player.BLANK) return winner;
        winner = checkDiagonals();
        return winner;
    }

    private Player checkRows() {
        for (int i = 0; i < DIM; i++) {
            if (grid[i][0] != Player.BLANK && allEqual(grid[i])) {
                return grid[i][0];
            }
        }
        return Player.BLANK;
    }

    private Player checkColumns() {
        for (int col = 0; col < DIM; col++) {
            Player candidate = grid[0][col];
            if (candidate != Player.BLANK) {
                boolean isWin = true;
                for (int row = 1; row < DIM; row++) {
                    if (grid[row][col] != candidate) {
                        isWin = false;
                        break;
                    }
                }
                if (isWin) return candidate;
            }
        }
        return Player.BLANK;
    }

    private Player checkDiagonals() {
        // Main diagonal
        Player candidate = grid[0][0];
        if (candidate != Player.BLANK) {
            boolean isWin = true;
            for (int i = 1; i < DIM; i++) {
                if (grid[i][i] != candidate) {
                    isWin = false;
                    break;
                }
            }
            if (isWin) return candidate;
        }

        // Anti-diagonal
        candidate = grid[0][DIM - 1];
        if (candidate != Player.BLANK) {
            boolean isWin = true;
            for (int i = 1; i < DIM; i++) {
                if (grid[i][DIM - 1 - i] != candidate) {
                    isWin = false;
                    break;
                }
            }
            if (isWin) return candidate;
        }

        return Player.BLANK;
    }

    /** Checks if all elements in a row are the same. */
    private boolean allEqual(Player[] row) {
        Player first = row[0];
        for (Player p : row) {
            if (p != first) return false;
        }
        return true;
    }

    /** Checks if the game is a tie. */
    public boolean checkForTie() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (grid[i][j] == Player.BLANK) {
                    return false; // Available move found
                }
            }
        }
        return checkForWin() == Player.BLANK; // Tie if no winner
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}

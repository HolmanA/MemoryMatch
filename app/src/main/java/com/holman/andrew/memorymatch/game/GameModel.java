package com.holman.andrew.memorymatch.game;

import com.holman.andrew.memorymatch.Constants;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Game logic class for the Memory Match game
 *
 * @see GameViewController
 * @see GameActivity
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 1.0
 */
class GameModel {
	/**
	 * Integer representation of the number of columns and rows in the game
	 */
	static final int size = 4;

	/**
	 * Millisecond delay before decrementing score during play on easy difficulty
	 */
	static final long EASY_TICK_DELAY_MILLIS = 2000;

	/**
	 * Millisecond delay before decrementing score during play on hard difficulty
	 */
	static final long HARD_TICK_DELAY_MILLIS = 1000;

	private String[][] board;
	private int[][] selection;
	private int[][] timesViewed;
	private int score;
	private int difficulty;

	/**
	 * The unicode emojis used as tile symbols
	 */
	private static final String[] symbols = {
			"\uD83D\uDC2D", //Mouse face
			"\uD83D\uDC35", //Monkey face
			"\uD83D\uDC3C", //Panda face
			"\uD83D\uDC37", //Pig face
			"\uD83D\uDC36", //Dog face
			"\uD83D\uDC31", //Cat face
			"\uD83D\uDC30", //Rabbit face
			"\uD83D\uDC2E"  //Cow face
	};

	/**
	 * Constructor
	 *
	 * @param diff  The integer representation of this game's difficulty. See {@link Constants} for
	 *              expected values.
	 */
	GameModel(int diff) {
		difficulty = diff;
		board = new String[size][size];
		selection = new int[2][2];
		timesViewed = new int[size][size];
	}

	/**
	 * Initializes the game board
	 */
	private void fillBoard() {
		LinkedList<String> tileSymbols = new LinkedList<>();

		switch (difficulty) {
			case Constants.DIFFICULTY_EASY:
				/* Easy uses 2 pairs per symbol */
				for (int i = 0; i < ((size * size) / 4); i++) {
					String symbol = symbols[i];
					tileSymbols.add(symbol);
					tileSymbols.add(symbol);
					tileSymbols.add(symbol);
					tileSymbols.add(symbol);
				}
				break;
			case Constants.DIFFICULTY_HARD:
				/* Hard uses 1 pair per symbol */
				for (int i = 0; i < ((size * size) / 2); i++) {
					String symbol = symbols[i];
					tileSymbols.add(symbol);
					tileSymbols.add(symbol);
				}
				break;
		}

		Collections.shuffle(tileSymbols);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = tileSymbols.removeFirst();
				timesViewed[i][j] = 0;
			}
		}
	}

	/**
	 * Retrieves the Unicode symbol for the tile at row i and column j
	 *
	 * @param i  The row index of the tile
	 * @param j  The column index of the tile
	 * @return  A String containing the Unicode symbol for the specified tile
	 */
	String getSymbol(int i, int j) {
		return (board[i][j]);
	}

	/**
	 * Begins a new game
	 */
	void startGame() {
		fillBoard();
		resetSelection();
		score = 0;
	}

	/**
	 * Attempts to assign tile coordinates to the selection array
	 * <p>
	 *     A selection is valid if it is either the first of two selected tiles, or if it is a
	 *     different tile than the first selected tile.
	 * </p>
	 *
	 * @param i  The row index of the selected tile
	 * @param j  The column index of the selected tile
	 * @return  True if the selection is valid, false if invalid
	 */
	boolean makeSelection(int i, int j) {
		/* -1 represents no tile selected */
		if (selection[0][0] == -1 && selection[0][1] == -1) {
			selection[0][0] = i;
			selection[0][1] = j;
			return true;
		} else if (selection[1][0] == -1 && selection[1][1] == -1) {
			/* Prevents selecting the same tile */
			if (selection[0][0] != i || selection[0][1] != j) {
				selection[1][0] = i;
				selection[1][1] = j;
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the selection array
	 */
	void resetSelection() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				selection[i][j] = -1;
			}
		}
	}


	/**
	 * Checks if the two selected tiles match
	 *
	 * @return  True if the selected tiles match, false otherwise
	 */
	boolean isMatch() {
		int x1 = selection[0][0];
		int y1 = selection[0][1];
		int x2 = selection[1][0];
		int y2 = selection[1][1];
		return (board[x1][y1].equals(board[x2][y2]));
	}

	/**
	 * Removes the selected pair of tiles from the board and resets the selected tiles
	 */
	void removePair() {
		int x1 = selection[0][0];
		int y1 = selection[0][1];
		int x2 = selection[1][0];
		int y2 = selection[1][1];
		board[x1][y1] = null;
		board[x2][y2] = null;
		resetSelection();
	}

	/**
	 * Increases the player's score
	 * <p>
	 *     The player receives 20 points for successfully matching two tiles
	 * </p>
	 */
	void increaseScore() {
		score += 20;
	}

	/**
	 * Decreases the player's score
	 * <p>
	 *     The player loses 5 points for each time the selected tiles have been previously revealed.
	 * </p>
	 */
	void decreaseScore() {
		int x1 = selection[0][0];
		int y1 = selection[0][1];
		int x2 = selection[1][0];
		int y2 = selection[1][1];
		score -= (5 * (timesViewed[x1][y1] + timesViewed[x2][y2]));

		/* Ensure the score does not become negative */
		score = (score < 0) ? 0 : score;
		timesViewed[x1][y1] += 1;
		timesViewed[x2][y2] += 1;
	}

	/**
	 * Decrements the user's score
	 * <p>
	 *     This method is called by TimerTask in {@link GameActivity#start()} using either
	 *     {@link #EASY_TICK_DELAY_MILLIS} or {@link #HARD_TICK_DELAY_MILLIS} to set how often this
	 *     method is called. This encourages players to solve the puzzle quickly, as their score
	 *     decreases based on time, to a minimum of 0.
	 * </p>
	 *
	 * @return The decremented integer score
	 */
	int tickScore() {
		return (score > 0) ? --score : 0;
	}

	/**
	 * Checks the board for any remaining un-matched tiles and returns the result
	 *
	 * @return  Boolean value representing whether the game has finished
	 */
	boolean isOver() {
		for (int i = 0; i < GameModel.size; i++) {
			for (int j = 0; j < GameModel.size; j++) {
				if (board[i][j] != null) {
					return false;
				}
			}
		}
		return true;
	}

	int[][] getSelections() {
		return selection;
	}

	int getScore() {
		return score;
	}

	int getDifficulty() {
		return difficulty;
	}
}

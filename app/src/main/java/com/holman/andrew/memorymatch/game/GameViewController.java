package com.holman.andrew.memorymatch.game;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.holman.andrew.memorymatch.Constants;
import com.holman.andrew.memorymatch.R;

import java.util.Random;

/**
 * Custom GridLayout used to display data from {@link GameModel}
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 1.0
 */
public class GameViewController extends GridLayout {
	private Point displaySize;
	private int boardSize;
	private Button[][] tileButtons;
	private TextView score;

	/**
	 * Constructor
	 *
	 * @param context  The current application Context
	 * @param displaySize  A Point object containing the device's display size
	 * @param boardSize  The number of rows / columns in the game board
	 * @param difficulty  The integer difficulty of this game instance. See {@link Constants} for
	 *                    expected values
	 * @param tileListener  A 2-dimensional array of {@link GameActivity.TileHandler} objects
	 */
	public GameViewController(Context context, Point displaySize, int boardSize,
	                          int difficulty, OnClickListener[][] tileListener) {
		super(context);

		this.displaySize = displaySize;
		this.boardSize = boardSize;

		setColumnCount(this.boardSize);
		setRowCount(this.boardSize + 2);
		setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

		int tileWidth = this.displaySize.x / this.boardSize;

		initializeTiles(context, tileWidth, difficulty, tileListener);
		initializeScoreView(context, tileWidth);
	}

	/**
	 * Animates the removal of all game tiles
	 */
	public void animateDestroy() {
		Random random = new Random();

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				int duration = 1000 + random.nextInt(500);
				String propertyName = "translationX";
				int toTranslation = displaySize.x;

				switch (duration % 3) {
					case 0:
						propertyName = "translationY";
						toTranslation = -displaySize.y;
						break;
					case 1:
						toTranslation = -displaySize.x;
						break;
				}

				/* Animate tiles */
				ObjectAnimator animator = ObjectAnimator.ofFloat(tileButtons[i][j], propertyName, toTranslation);
				animator.setDuration(duration);
				animator.start();
			}
		}

		/* Animate score */
		ObjectAnimator animator = ObjectAnimator.ofFloat(score, "translationY", displaySize.x);
		animator.setDuration(1000);
		animator.start();
	}

	/**
	 * Sets the text for the specified tile
	 *
	 * @param s  The String to apply to the tile
	 * @param i  The row index of the tile
	 * @param j  The column index of the tile
	 */
	public void setTileText(String s, int i, int j) {
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(300);

		tileButtons[i][j].setText(s);
		tileButtons[i][j].startAnimation(animation);
	}

	/**
	 * Removes the text from the specified tile
	 *
	 * @param i  The row index of the tile
	 * @param j  The column index of the tile
	 */
	public void removeTileText(int i, int j) {
		tileButtons[i][j].setText("");
	}

	/**
	 * Hides the specified tile
	 *
	 * @param i  The row index of the tile
	 * @param j  The column index of the tile
	 */
	public void hideTile(int i, int j) {
		AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(300);
		tileButtons[i][j].startAnimation(animation);
		tileButtons[i][j].setVisibility(View.INVISIBLE);
	}

	/**
	 * Displays the specified game tile
	 *
	 * @param i  The row index of the tile
	 * @param j  The column index of the tile
	 */
	public void showTile(int i, int j) {
		tileButtons[i][j].setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the score to the specified String
	 *
	 * @param s  The String to apply to the score View
	 */
	public void setScoreText(String s) {
		score.setText(s);
	}

	/**
	 * Creates, sets onClick listeners for, displays, and animates the game tiles
	 *
	 * @param context  The current application Context
	 * @param tileWidth  The width of a single game tile
	 * @param difficulty  The integer difficulty of this game instance. See {@link Constants} for
	 *                    expected values.
	 * @param tileListener  A 2-dimensional array of {@link GameActivity.TileHandler} objects
	 */
	private void initializeTiles(Context context, int tileWidth, int difficulty, OnClickListener[][] tileListener) {
		Random random = new Random();

		/* Set tile colors based on difficulty */
		int tileColor;

		if (difficulty == Constants.DIFFICULTY_EASY) {
			tileColor = ContextCompat.getColor(context, R.color.easy_green);
		} else {
			tileColor = ContextCompat.getColor(context, R.color.hard_red);
		}

		/* Construct and add tile buttons to view */
		tileButtons = new Button[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				int duration = 1000 + random.nextInt(500);
				String propertyName = "translationX";

				tileButtons[i][j] = new Button(context);
				tileButtons[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
				tileButtons[i][j].setOnClickListener(tileListener[i][j]);
				tileButtons[i][j].setVisibility(View.INVISIBLE);
				tileButtons[i][j].getBackground().setColorFilter(tileColor, PorterDuff.Mode.MULTIPLY);

				switch (duration % 3) {
					case 0:
						propertyName = "translationY";
						tileButtons[i][j].setTranslationY(0 - displaySize.y);
						break;
					case 1:
						tileButtons[i][j].setTranslationX(0 - displaySize.x);
						break;
					case 2:
						tileButtons[i][j].setTranslationX(displaySize.x);
						break;
				}
				addView(tileButtons[i][j], tileWidth, tileWidth);

				/* Animate tiles */
				ObjectAnimator animator = ObjectAnimator.ofFloat(tileButtons[i][j], propertyName, 0f);
				animator.setDuration(duration);
				animator.start();
			}
		}
	}

	/**
	 * Creates, displays, and animates the View responsible for displaying the player's score
	 *
	 * @param context  The current application Context
	 * @param tileWidth  The width of a single game tile
	 */
	private void initializeScoreView(Context context, int tileWidth) {
		score = new TextView(context);
		Spec rowSpec = GridLayout.spec(boardSize, 0);
		Spec columnSpec = GridLayout.spec(0, boardSize);
		LayoutParams lpStatus = new LayoutParams(rowSpec, columnSpec);
		score.setLayoutParams(lpStatus);
		score.setWidth(displaySize.x);
		score.setHeight(tileWidth);
		score.setTranslationY(displaySize.y);
		score.setGravity(Gravity.CENTER);
		score.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentDark));
		score.setTextSize(40);
		score.setTextColor(ContextCompat.getColor(context, R.color.off_white));
		addView(score);

		/* Animate score */
		ObjectAnimator animator = ObjectAnimator.ofFloat(score, "translationY", 0f);
		animator.setDuration(1000);
		animator.start();
	}
}

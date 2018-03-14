package com.holman.andrew.memorymatch.game;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.holman.andrew.memorymatch.Constants;
import com.holman.andrew.memorymatch.R;
import com.holman.andrew.memorymatch.leaderboard.LeaderboardActivity;
import com.holman.andrew.memorymatch.menu.MenuActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity used to display the memory match game
 * <p>
 *     This class makes frequent use of both {@link GameModel} and {@link GameViewController}.
 * </p>
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 1.0
 */
public class GameActivity extends AppCompatActivity {
	private static final String TAG = "GameActivity";

	/**
	 * Identifier String for Score intent extra
	 */
	public static final String EXTRA_SCORE = "com.holman.andrew.memorymatch.game.SCORE";

	/**
	 * Identifier String for Difficulty intent extra
	 */
	public static final String EXTRA_DIFFICULTY = "com.holman.andrew.memorymatch.game.DIFFICULTY";

	/**
	 * Identifier String for Insert intent extra
	 */
	public static final String EXTRA_INSERT = "com.holman.andrew.memorymatch.game.INSERT";

	private GameModel model;
	private GameViewController gameController;

	/* Max number of tiles allowed to be selected at once */
	private final int maxSelections = 2;

	/* Current number of selected tiles */
	private int selectionCount;
	private Timer gameTimer;

	/**
	 * Initializes the Activity
	 *
	 * @param savedInstanceState  Bundle of saved state used for activity re-initialization
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Enter/Exit transitions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
		}

		selectionCount = 0;
		gameTimer = new Timer();

		Intent intent = getIntent();
		int difficulty = intent.getIntExtra(MenuActivity.EXTRA_DIFFIICULTY, Constants.DIFFICULTY_EASY);

		model = new GameModel(difficulty);

		Point displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);

		TileHandler[][] tHandler = new TileHandler[GameModel.size][GameModel.size];
		initializeTileHandlers(tHandler);

		gameController = new GameViewController(this, displaySize,
				GameModel.size, difficulty, tHandler);

		setContentView(gameController);
		start();
	}

	/**
	 * Animates destruction of game tiles and calls the default navigation back button
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		gameController.animateDestroy();
	}

	/**
	 * Fills the array with {@link TileHandler} objects
	 *
	 * @param tHandler  An empty NxN array of type {@link TileHandler}
	 */
	private void initializeTileHandlers(TileHandler[][] tHandler) {
		for (int i = 0; i < GameModel.size; i++) {
			for (int j = 0; j < GameModel.size; j++) {
				tHandler[i][j] = new TileHandler(i, j);
			}
		}
	}

	/**
	 * Begins game execution
	 *
	 * @see GameModel
	 */
	public void start() {
		long tickDelay;

		switch (model.getDifficulty()) {
			case Constants.DIFFICULTY_EASY :
				tickDelay = GameModel.EASY_TICK_DELAY_MILLIS;
				break;
			case Constants.DIFFICULTY_HARD :
				tickDelay = GameModel.HARD_TICK_DELAY_MILLIS;
				break;
			default :
				Log.wtf(TAG, "Unknown difficulty found: " + Integer.toString(model.getDifficulty()));
				throw new RuntimeException("Invalid variable: " + Integer.toString(model.getDifficulty()));
		}

		/* Periodically decrease score according to tickDelay */
		gameTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gameController.setScoreText(getString(R.string.score_fmt, model.tickScore()));
					}
				});
			}
		}, 0, tickDelay);

		model.startGame();
		showTiles();
	}

	/**
	 * Displays all game tiles
	 *
	 * @see GameViewController#showTile(int, int)
	 */
	private void showTiles() {
		for (int i = 0; i < GameModel.size; i++) {
			for (int j = 0; j < GameModel.size; j++) {
				gameController.setTileText("", i, j);
				gameController.showTile(i, j);
			}
		}
	}

	/**
	 * Launches {@link LeaderboardActivity}
	 * <p>
	 *     Called when all tiles have been successfully matched
	 * </p>
	 */
	private void endGame() {
		Intent scoreIntent = new Intent(getApplicationContext(), LeaderboardActivity.class);
		scoreIntent.putExtra(EXTRA_SCORE, model.getScore());
		scoreIntent.putExtra(EXTRA_DIFFICULTY, model.getDifficulty());
		scoreIntent.putExtra(EXTRA_INSERT, true);
		finish();
		startActivity(scoreIntent);
	}

	/**
	 * Removes the tiles from the board, increases score, and checks if the game is now over
	 *
	 * @param selections  Two dimensional integer array holding the row and column indices of the
	 *                    two selected game tiles
	 * @see GameModel
	 * @see GameViewController
	 */
	private void match(int[][] selections) {
		gameController.hideTile(selections[0][0], selections[0][1]);
		gameController.hideTile(selections[1][0], selections[1][1]);
		model.removePair();
		model.increaseScore();
		gameController.setScoreText(getString(R.string.score_fmt, model.getScore()));

		if (model.isOver()) {
			endGame();
		}
	}

	/**
	 * Re-flips the tiles to hide their symbols, decreases score, and resets selected tiles
	 *
	 * @param selections  Two dimensional integer array holding the row and column indices of the
	 *                    two selected game tiles
	 * @see GameModel
	 * @see GameViewController
	 */
	private void misMatch(int[][] selections) {
		gameController.removeTileText(selections[0][0], selections[0][1]);
		gameController.removeTileText(selections[1][0], selections[1][1]);
		model.decreaseScore();
		model.resetSelection();
		gameController.setScoreText(getString(R.string.score_fmt, model.getScore()));
	}

	/**
	 * Event handler responsible for responding to tile button presses
	 */
	private class TileHandler implements View.OnClickListener {
		private int row;
		private int col;

		/**
		 * Constructor
		 *
		 * @param i  Row index of corresponding tile
		 * @param j  Column index of corresponding tile
		 */
		private TileHandler(int i, int j) {
			super();
			row = i;
			col = j;
		}

		/**
		 * Responds to the user selecting a game tile
		 *
		 * @param v The View that called this method from its onClick attribute
		 * @see GameModel
		 * @see GameViewController
		 */
		public void onClick(View v) {
			/* Prevent more than 2 tiles being selected in rapid succession */
			if (selectionCount < maxSelections) {

				/* model.makeSelection returns true if the selection was valid
					false if invalid */
				if (model.makeSelection(row, col)) {
					selectionCount++;

					/* Flip tile */
					String symbol = model.getSymbol(row, col);
					gameController.setTileText(symbol, row, col);

					/* Pair selected */
					if (selectionCount == maxSelections) {
						Handler h = new Handler();
						/* Delay before re-flipping or removing selected tiles */
						h.postDelayed(new Runnable() {
							public void run() {
								int[][] selections = model.getSelections();
								if (model.isMatch()) {
									match(selections);
								} else {
									misMatch(selections);
								}
								/* Only reset the selection count after valid pair was tested */
								selectionCount = 0;
							}
						}, 500);
					}
				}
			}
		}
	}
}

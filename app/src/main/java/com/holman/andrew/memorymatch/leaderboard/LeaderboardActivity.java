package com.holman.andrew.memorymatch.leaderboard;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.holman.andrew.memorymatch.Constants;
import com.holman.andrew.memorymatch.R;
import com.holman.andrew.memorymatch.game.GameActivity;
import com.holman.andrew.memorymatch.provider.LeaderboardContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity responsible for displaying and switching between game leaderboards
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class LeaderboardActivity extends AppCompatActivity {
	/**
	 * Identifier String for bundle arguments
	 */
	public static String BUNDLE_DIFFICULTY = "com.holman.andrew.memorymatch.leaderboard.DIFFICULTY";

	private static String TAG = "LeaderboardActivity";

	/**
	 * Initializes the Activity
	 * <p>
	 *     Calls to {@link #insertScore(int, int)} if instructed by the previous activity.
	 *     Initializes the {@link LeaderboardListFragment} to display through a call to
	 *     {@link #initializeLeaderboardFragment(int)}.
	 * </p>
	 *
	 * @param savedInstanceState  Bundle of saved state used for activity re-initialization
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		Intent intent = getIntent();
		int score = intent.getIntExtra(GameActivity.EXTRA_SCORE, 0);
		int difficulty = intent.getIntExtra(GameActivity.EXTRA_DIFFICULTY,
				Constants.DIFFICULTY_EASY);
		boolean insert = intent.getBooleanExtra(GameActivity.EXTRA_INSERT, false);

		TextView scoreView = findViewById(R.id.previous_score);
		scoreView.setText(getString(R.string.score_fmt, score));


		if (insert) {
			int color = 0;
			switch (difficulty) {
				case Constants.DIFFICULTY_EASY:
					color = ContextCompat.getColor(this, R.color.easy_green);
					break;
				case Constants.DIFFICULTY_HARD:
					color = ContextCompat.getColor(this, R.color.hard_red);
					break;
			}
			scoreView.setBackgroundColor(color);
			scoreView.setVisibility(View.VISIBLE);
			insertScore(score, difficulty);
		}

		initializeLeaderboardFragment(difficulty);
	}

	/**
	 * Calls the default back press navigation button
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void endActivity(View view) {
		super.onBackPressed();
	}

	/**
	 * Replaces the currently displayed leaderboard with the leaderboard selected by the user
	 * <p>
	 *     Removes the currently displayed {@link LeaderboardListFragment} and calls
	 *     {@link #initializeLeaderboardFragment(int)} to display the user-selected leaderboard.
	 * </p>
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick}
	 */
	public void swapLeaderboard(View view) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.remove(fragmentManager.findFragmentById(R.id.leaderboard_fragment_container))
				.commit();

		int selectedDifficulty = Integer.parseInt(view.getTag().toString());
		initializeLeaderboardFragment(selectedDifficulty);
	}

	/**
	 * Sets the leaderboard fragment to display
	 * <p>
	 *     Instantiates and displays a new {@link LeaderboardListFragment} and highlights the button
	 *     corresponding to it.
	 * </p>
	 *
	 * @param difficulty  The integer representation of the leaderboard to display's difficulty.
	 *                    See {@link Constants} for expected values.
	 */
	private void initializeLeaderboardFragment(int difficulty) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Button activeTab;
		Button inactiveTab;
		Drawable activeBackground;

		LeaderboardListFragment leaderboard = new LeaderboardListFragment();
		Bundle args = new Bundle();

		switch (difficulty) {
			case Constants.DIFFICULTY_EASY:
				args.putInt(BUNDLE_DIFFICULTY, Constants.DIFFICULTY_EASY);
				activeTab = findViewById(R.id.switch_easy_leaderboard);
				inactiveTab = findViewById(R.id.switch_hard_leaderboard);
				activeBackground = ResourcesCompat.getDrawable(getResources(),
						R.drawable.leaderboard_easy_tab_background, null);
				break;
			case Constants.DIFFICULTY_HARD:
				args.putInt(BUNDLE_DIFFICULTY, Constants.DIFFICULTY_HARD);
				activeTab = findViewById(R.id.switch_hard_leaderboard);
				inactiveTab = findViewById(R.id.switch_easy_leaderboard);
				activeBackground = ResourcesCompat.getDrawable(getResources(),
						R.drawable.leaderboard_hard_tab_background, null);
				break;
			default:
				Log.wtf(TAG, "Unknown difficulty found: " + Integer.toString(difficulty));
				throw new RuntimeException("Invalid variable: " + Integer.toString(difficulty));
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			activeTab.setBackground(activeBackground);
		} else {
			inactiveTab.setBackgroundDrawable(activeBackground);
		}
		inactiveTab.setBackgroundColor(
				ContextCompat.getColor(this, R.color.colorPrimaryDark));
		activeTab.setEnabled(false);
		inactiveTab.setEnabled(true);

		leaderboard.setArguments(args);
		fragmentManager.beginTransaction()
				.add(R.id.leaderboard_fragment_container, leaderboard)
				.commit();
	}

	/**
	 * Inserts a score to the leaderboard database
	 * <p>
	 *     Inserts the score, difficulty, and current date into
	 *     {@link com.holman.andrew.memorymatch.provider.LeaderboardProvider}
	 * </p>
	 *
	 * @param score  The integer score to be inserted
	 * @param difficulty  An integer representation of the difficulty the score was achieved on.
	 *                    See {@link Constants} for expected values.
	 * @see LeaderboardContract
	 */
	private void insertScore(int score, int difficulty) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.US);

		ContentValues values = new ContentValues();
		values.put(LeaderboardContract.Scores.SCORE, score);
		values.put(LeaderboardContract.Scores.DIFFICULTY, difficulty);
		values.put(LeaderboardContract.Scores.DATE_TIME, dateFormat.format(new Date()));

		try {
			getContentResolver().insert(LeaderboardContract.Scores.SCORES_CONTENT_URI, values);
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			Toast toast = Toast.makeText(getApplicationContext(),
					"Failed to insert score", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}

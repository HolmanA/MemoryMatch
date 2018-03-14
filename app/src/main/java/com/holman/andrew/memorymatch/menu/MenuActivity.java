package com.holman.andrew.memorymatch.menu;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.holman.andrew.memorymatch.R;
import com.holman.andrew.memorymatch.game.GameActivity;
import com.holman.andrew.memorymatch.game.RulesActivity;
import com.holman.andrew.memorymatch.leaderboard.LeaderboardActivity;

/**
 * Activity representing the main menu of the game
 * <p>
 *     This Activity holds Buttons allowing navigation to all other Activities in the application
 * </p>
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class MenuActivity extends AppCompatActivity {
	/**
	 * Identifier String for difficulty intent extra
	 */
	public static final String EXTRA_DIFFIICULTY = "com.holman.andrew.memorymatch.menu.DIFFICULTY";

	/* Point object holding this devices display size */
	private static Point displaySize;
	private static final String TAG = "MenuActivity";

	/**
	 * Initializes the Activity
	 *
	 * @param savedInstanceState  Bundle of saved state used for activity re-initialization
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Enter/Exit transitions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
		}

		setContentView(R.layout.activity_menu);

		displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
	}

	/**
	 * Called when returning to this Activity
	 * <p>
	 *     Hides the Easy and Hard difficulty buttons and shows the New Game Button. Reverses the
	 *     animations of {@link #selectDifficulty(View)}.
	 * </p>
	 */
	@Override
	public void onResume() {
		super.onResume();

		Button newGameButton = findViewById(R.id.newGameButton);
		Button easyButton = findViewById(R.id.easyButton);
		Button hardButton = findViewById(R.id.hardButton);

		int offset = newGameButton.getHeight();

		PropertyValuesHolder pvhAlpha1 = PropertyValuesHolder.ofFloat("alpha", 1.0f);
		PropertyValuesHolder pvhAlpha0 = PropertyValuesHolder.ofFloat("alpha", 0.0f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0f);
		PropertyValuesHolder pvhOffsetY = PropertyValuesHolder.ofFloat("translationY", -offset);

		ObjectAnimator newGameAnimator = ObjectAnimator.ofPropertyValuesHolder(newGameButton, pvhAlpha1, pvhY);
		ObjectAnimator easyAnimator = ObjectAnimator.ofPropertyValuesHolder(easyButton, pvhAlpha0, pvhOffsetY);
		ObjectAnimator hardAnimator = ObjectAnimator.ofPropertyValuesHolder(hardButton, pvhAlpha0, pvhOffsetY);
		long duration = 500;
		newGameAnimator.setDuration(duration);
		easyAnimator.setDuration(duration);
		hardAnimator.setDuration(duration);
		easyAnimator.start();
		hardAnimator.start();
		newGameAnimator.start();
	}

	/**
	 * Hides the New Game Button and displays the Easy and Hard difficulty buttons
	 * <p>
	 *     Implements custom animations for hiding and displaying the buttons
	 * </p>
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void selectDifficulty(View view) {
		Button newGameButton = findViewById(R.id.newGameButton);
		Button easyButton = findViewById(R.id.easyButton);
		Button hardButton = findViewById(R.id.hardButton);

		int offset = newGameButton.getHeight();

		easyButton.setVisibility(View.VISIBLE);
		hardButton.setVisibility(View.VISIBLE);
		easyButton.setAlpha(0f);
		hardButton.setAlpha(0f);
		easyButton.setTranslationY(-offset);
		hardButton.setTranslationY(-offset);

		PropertyValuesHolder pvhAlpha1 = PropertyValuesHolder.ofFloat("alpha", 1.0f);
		PropertyValuesHolder pvhAlpha0 = PropertyValuesHolder.ofFloat("alpha", 0.0f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0f);
		PropertyValuesHolder pvhOffsetY = PropertyValuesHolder.ofFloat("translationY", offset);

		ObjectAnimator newGameAnimator = ObjectAnimator.ofPropertyValuesHolder(newGameButton, pvhAlpha0, pvhOffsetY);
		ObjectAnimator easyAnimator = ObjectAnimator.ofPropertyValuesHolder(easyButton, pvhAlpha1, pvhY);
		ObjectAnimator hardAnimator = ObjectAnimator.ofPropertyValuesHolder(hardButton, pvhAlpha1, pvhY);
		long duration = 500;
		newGameAnimator.setDuration(duration);
		easyAnimator.setDuration(duration);
		hardAnimator.setDuration(duration);
		easyAnimator.start();
		hardAnimator.start();
		newGameAnimator.start();
	}

	/**
	 * Launches {@link GameActivity} with Transition Animations if supported by this device
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void startGameActivity(View view) {
		int difficulty = Integer.parseInt(view.getTag().toString());
		Intent intent = new Intent(getApplicationContext(), GameActivity.class);
		intent.putExtra(EXTRA_DIFFIICULTY, difficulty);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Slide enter = new Slide(Gravity.TOP);
			enter.excludeTarget(android.R.id.statusBarBackground, true);
			enter.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setEnterTransition(enter);

			Slide exit = new Slide(Gravity.BOTTOM);
			exit.excludeTarget(android.R.id.statusBarBackground, true);
			exit.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setExitTransition(exit);

			startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
		} else {
			startActivity(intent);
		}
	}

	/**
	 * Launches {@link RulesActivity} with Transition Animations if supported by this device
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void startRulesActivity(View view) {
		Intent intent = new Intent(getApplicationContext(), RulesActivity.class);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Slide enter = new Slide(Gravity.RIGHT);
			enter.excludeTarget(android.R.id.statusBarBackground, true);
			enter.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setEnterTransition(enter);

			Slide exit = new Slide(Gravity.LEFT);
			exit.excludeTarget(android.R.id.statusBarBackground, true);
			exit.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setExitTransition(exit);

			startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
		} else {
			startActivity(intent);
		}
	}

	/**
	 * Launches {@link LeaderboardActivity}
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void startLeaderboardActivity(View view) {
		Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
		startActivity(intent);
	}
}

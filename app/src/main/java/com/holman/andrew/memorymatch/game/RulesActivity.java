package com.holman.andrew.memorymatch.game;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.holman.andrew.memorymatch.R;

/**
 * Activity responsible for displaying the game rules
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class RulesActivity extends AppCompatActivity {
	private static final String TAG = "HowToPlayActivity";

	/**
	 * Initializes the Activity
	 *
	 * @param savedInstanceState  Bundle of saved state used for activity re-initialization
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Enter/Exit transitions */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

			Slide enter = new Slide(Gravity.RIGHT);
			enter.excludeTarget(android.R.id.statusBarBackground, true);
			enter.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setEnterTransition(enter);

			Slide exit = new Slide(Gravity.LEFT);
			exit.excludeTarget(android.R.id.statusBarBackground, true);
			exit.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setExitTransition(exit);
		}

		setContentView(R.layout.activity_rules);
	}

	/**
	 * Calls the default back press navigation button
	 *
	 * @param view  The View responsible for calling this method in its {@code onClick} attribute
	 */
	public void endActivity(View view) {
		super.onBackPressed();
	}
}

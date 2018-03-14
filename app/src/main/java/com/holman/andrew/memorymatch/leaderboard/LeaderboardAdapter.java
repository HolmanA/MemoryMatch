package com.holman.andrew.memorymatch.leaderboard;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holman.andrew.memorymatch.R;
import com.holman.andrew.memorymatch.provider.LeaderboardContract;

import java.util.Locale;

/**
 * Custom Cursor Adapter for interfacing with a Leaderboard Provider
 * <p>
 *     Binds the data of a Cursor obtained from a Leaderboard Provider with a View representing a
 *     row in the {@link LeaderboardListFragment}.
 * </p>
 *
 * @see LeaderboardContract
 * @see com.holman.andrew.memorymatch.provider.LeaderboardProvider
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class LeaderboardAdapter extends CursorAdapter {
	private static final String TAG = "LeaderboardAdapter";

	/**
	 * Constructor
	 *
	 * @param context  The current application Context
	 * @param cursor  The Cursor associated with this adapter instance
	 */
	LeaderboardAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
	}

	/**
	 * Inflates a new view used to hold the data of a Cursor
	 *
	 * @param context  The current application Context
	 * @param cursor  The Cursor attached associated with this View
	 * @param parent  The parent ViewGroup of the returned View
	 * @return The inflated View
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
	}

	/**
	 * Binds the data in the specified Cursor to the specified View
	 *
	 * @param view  The View with which to bind the data
	 * @param context  The current application Context
	 * @param cursor  The Cursor providing the data to bind
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final int rank = cursor.getPosition() + 1;
		final int score = cursor.getInt(cursor.getColumnIndex(LeaderboardContract.Scores.SCORE));
		final String dateTime = cursor.getString(cursor.getColumnIndex(LeaderboardContract.Scores.DATE_TIME));

		TextView rankView = view.findViewById(R.id.score_rank);
		TextView scoreView = view.findViewById(R.id.score_value);
		TextView dateView = view.findViewById(R.id.score_date);

		rankView.setText(String.format(Locale.US, "%d", rank));
		scoreView.setText(String.format(Locale.US, "%d", score));
		dateView.setText(dateTime);
	}
}

package com.holman.andrew.memorymatch.leaderboard;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holman.andrew.memorymatch.Constants;
import com.holman.andrew.memorymatch.R;
import com.holman.andrew.memorymatch.provider.LeaderboardContract;

/**
 * List Fragment used to display a single leaderboard
 * <p>
 *     Implements {@code LoaderManager.LoaderCallbacks<Cursor>} interface to enable synchronization
 *     with a {@link LeaderboardAdapter}. This allows for asynchronous queries to
 *     {@link com.holman.andrew.memorymatch.provider.LeaderboardProvider}.
 * </p>
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class LeaderboardListFragment extends ListFragment
		implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "LeaderboardListFragment";

	/* Difficulty of this leaderboard instance */
	private int difficulty;
	private LeaderboardAdapter adapter;

	/**
	 * Creates a Leaderboard List Fragment and sets the {@link LeaderboardAdapter}
	 *
	 * @param savedInstanceState Bundle of saved state used for fragment re-initialization
	 * @see Constants
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		difficulty = Constants.DIFFICULTY_EASY;

		Bundle bundle = getArguments();
		if (bundle != null) {
			difficulty = bundle.getInt(LeaderboardActivity.BUNDLE_DIFFICULTY);
		}

		adapter = new LeaderboardAdapter(getActivity(), null);
		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Inflates a View used to hold this LeaderboardListFragment instance
	 *
	 * @param inflater  LayoutInflater used to inflate the View
	 * @param group  Parent ViewGroup
	 * @param savedInstanceState  Unused parameter inherited from superclass
	 * @return  The inflated View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.leaderboard_list_fragment, group, false);
	}

	/* Projection used to query Leaderboard Provider */
	private final String[] LEADERBOARD_PROJECTION = {
			LeaderboardContract.Scores._ID,
			LeaderboardContract.Scores.SCORE,
			LeaderboardContract.Scores.DATE_TIME
	};

	/**
	 * Creates a Cursor Loader containing a query to the
	 * {@link com.holman.andrew.memorymatch.provider.LeaderboardProvider}
	 *
	 * @param id  Unused parameter inherited from superclass
	 * @param args  Unused parameter inherited from superclass
	 * @return  A Loader of type Cursor holding the results of this query
	 * @see LeaderboardContract
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = LeaderboardContract.Scores.SCORES_CONTENT_URI;

		String selection = LeaderboardContract.Scores.DIFFICULTY + " = ?";
		String[] selectionArgs = new String[]{Integer.toString(difficulty)};
		return new CursorLoader(getActivity(), uri,
				LEADERBOARD_PROJECTION, selection,
				selectionArgs, null);
	}

	/**
	 * Swaps a new Cursor into the {@link LeaderboardAdapter} when the previous Cursor is done
	 * loading
	 *
	 * @param loader  The relevant Cursor Loader
	 * @param data  The next Cursor to add to the {@link LeaderboardAdapter}
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	/**
	 * Resets previous Cursor. Makes all data in that Cursor unavailable
	 *
	 * @param loader  The relevant Cursor Loader
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}

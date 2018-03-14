package com.holman.andrew.memorymatch.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for {@link LeaderboardProvider}
 * <p>
 *     This class defines all content Uri's and table columns for {@link LeaderboardProvider}
 * </p>
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public final class LeaderboardContract {
	public static final String AUTHORITY = "com.holman.andrew.memorymatch.provider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	/**
	 * Content Uri and column Strings for the scores table
	 */
	public static class Scores implements BaseColumns {
		public static final Uri SCORES_CONTENT_URI =
				Uri.withAppendedPath(CONTENT_URI, LeaderboardOpenHelper.TABLE_SCORES);

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.holman.andrew.leaderboard_scores";

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.holman.andrew.leaderboard_scores";

		public static final String DATE_TIME = LeaderboardOpenHelper.SCORE_DATE_TIME;

		public static final String DIFFICULTY = LeaderboardOpenHelper.SCORE_DIFFICULTY;

		public static final String SCORE = LeaderboardOpenHelper.SCORE_VALUE;

		public static final String[] PROJECTION_ALL = {_ID, DATE_TIME, DIFFICULTY, SCORE};

		public static final String SORT_ORDER_DEFAULT = SCORE + " DESC";
	}
}

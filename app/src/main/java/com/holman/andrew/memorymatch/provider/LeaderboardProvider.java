package com.holman.andrew.memorymatch.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Content Provider used to access the leaderboard database
 * <p>
 *     This class is necessary for defining
 *     {@link com.holman.andrew.memorymatch.leaderboard.LeaderboardActivity} and implementing a
 *     Cursor Loader. This allows for asynchronous queries to the database.
 * </p>
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class LeaderboardProvider extends ContentProvider {
	private static final String TAG = "LeaderboardProvider";

	private static final int SCORE_LIST = 1;
	private static final int SCORE_ID = 2;
	private static final UriMatcher URI_MATCHER;

	private LeaderboardOpenHelper dbManager = null;

	/* Add all valid Uri's to the URI_MATCHER */
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(LeaderboardContract.AUTHORITY, "scores", SCORE_LIST);
		URI_MATCHER.addURI(LeaderboardContract.AUTHORITY, "scores/#", SCORE_ID);
	}

	/**
	 * Instantiates a {@link LeaderboardOpenHelper}
	 *
	 * @return Always returns true
	 */
	@Override
	public boolean onCreate() {
		dbManager = new LeaderboardOpenHelper(getContext());
		return true;
	}

	/**
	 * Queries the Leaderboard database
	 *
	 * @param uri  The Content Uri to query
	 * @param projections  The column projections to retrieve. See {@link LeaderboardContract} for
	 *                     values.
	 * @param selection  A selection criteria to apply when filtering rows
	 * @param selectionArgs  Replaces any '?' characters in selection with variables from
	 *                          selectionArgs
	 * @param sortOrder  String specifying the sort order of results
	 * @return
	 */
	@Override
	public Cursor query(@NonNull Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbManager.getWritableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(LeaderboardOpenHelper.TABLE_SCORES);

		switch (URI_MATCHER.match(uri)) {
			case SCORE_LIST:
				if (TextUtils.isEmpty(sortOrder)) {
					sortOrder = LeaderboardContract.Scores.SORT_ORDER_DEFAULT;
				}
				break;
			case SCORE_ID:
				builder.appendWhere(LeaderboardContract.Scores._ID + " = " + uri.getLastPathSegment());
			default:
				throw new IllegalArgumentException("Unsupported URI for selection: " + uri);
		}
		Cursor cursor = builder.query(db, projections, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	/**
	 * Inserts a row into the Leaderboard database
	 *
	 * @param uri  The Content Uri to insert into
	 * @param values  The ContentValues to be inserted
	 * @return  The Uri of the inserted row
	 */
	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {

		if (URI_MATCHER.match(uri) == SCORE_LIST) {
			SQLiteDatabase db = dbManager.getWritableDatabase();

			long id;
			if ((id = db.insert(LeaderboardOpenHelper.TABLE_SCORES, null, values)) == -1) {
				throw new SQLiteException("SQLite insertion failed");
			} else {
				return ContentUris.withAppendedId(uri, id);
			}
		} else {
			throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
		}
	}

	/**
	 * Performs a delete operation on the Leaderboard database
	 *
	 * @param uri  The Content Uri to delete from
	 * @param selection  A selection criteria to apply when filtering rows
	 * @param selectionArgs  Replaces any '?' characters in selection with variables from
	 *                          selectionArgs
	 * @return  The number of items deleted
	 */
	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbManager.getWritableDatabase();

		int count;
		switch (URI_MATCHER.match(uri)) {
			case SCORE_LIST:
				count = db.delete(LeaderboardOpenHelper.TABLE_SCORES, selection, selectionArgs);
				break;
			case SCORE_ID:
				String id = uri.getLastPathSegment();
				String where = LeaderboardOpenHelper.SCORE_ID + " = " + id;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				count = db.delete(LeaderboardOpenHelper.TABLE_SCORES, where, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI for deletion: " + uri);
		}

		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	/**
	 * Unused method inherited from superclass
	 */
	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	/**
	 * Retrieves the type of the specified Uri
	 *
	 * @param uri  The specified Uri
	 * @return  A String containing the Uri's type
	 */
	@Override
	public String getType(@NonNull Uri uri) {
		switch (URI_MATCHER.match(uri)) {
			case SCORE_LIST:
				return LeaderboardContract.Scores.CONTENT_TYPE;
			case SCORE_ID:
				return LeaderboardContract.Scores.CONTENT_ITEM_TYPE;
			default:
				return null;
		}
	}
}

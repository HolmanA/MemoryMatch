package com.holman.andrew.memorymatch.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * SQLiteOpenHelper class used to instantiate and update the Leaderboard database
 *
 * @author Andrew Holman
 * @version 2.0
 * @since 2.0
 */
public class LeaderboardOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = "LeaderboardOpenHelper";

	private static final String DATABASE_NAME = "leaderboardDB";

	private static final int DATABASE_VERSION = 2;

	static final String TABLE_SCORES = "scores";

	static final String SCORE_ID = BaseColumns._ID;
	static final String SCORE_DATE_TIME = "dateTime";
	static final String SCORE_DIFFICULTY = "difficulty";
	static final String SCORE_VALUE = "score";

	/**
	 * Constructor
	 *
	 * @param context  The current application Context
	 */
	LeaderboardOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creates the scores table
	 *
	 * @param db The SQLite Database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreateTable = "create table " + TABLE_SCORES + "( ";
		sqlCreateTable += SCORE_ID + " integer primary key autoincrement, ";
		sqlCreateTable += SCORE_DATE_TIME + " text, ";
		sqlCreateTable += SCORE_DIFFICULTY + " integer, ";
		sqlCreateTable += SCORE_VALUE + " integer)";

		db.execSQL(sqlCreateTable);
	}

	/**
	 * Drops the old table and creates a new one if the database version number has changed
	 *
	 * @param db  The SQLite Database
	 * @param oldVersion  Old database version number
	 * @param newVersion  New database version number
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_SCORES);
		onCreate(db);
	}
}

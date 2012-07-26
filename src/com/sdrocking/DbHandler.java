package com.sdrocking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

/**
 * Methods for storing scores into the db and retrieving them.
 * 
 * @author Siddhartha Dugar
 */
public class DbHandler {
	private Context context;
	private DbHelper dbHelper;
	private SQLiteDatabase sqlDatabase;

	private static final String DATABASE_NAME = "ticTacToe.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_SCORE = "SCORE";
	private static final String KEY_USERID = "USER_ID";
	private static final String KEY_USERNAME = "USER_NAME";
	private static final String KEY_PLAYED = "SCORE_PLAYED";
	private static final String KEY_WON = "SCORE_WIN";
	private static final String KEY_LOST = "SCORE_LOST";
	private static final String KEY_AGAINST = "AGAINST";

	public class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + TABLE_SCORE + " (" + KEY_USERID
					+ " integer primary key autoincrement, " + KEY_USERNAME
					+ " text not null, " + KEY_PLAYED + " integer not null, "
					+ KEY_WON + " integer not null, " + KEY_LOST
					+ " integer not null, " + KEY_AGAINST + " text not null);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TABLE_SCORE);
			onCreate(db);
		}
	}

	public DbHandler(Context context) {
		this.context = context;
	}

	public DbHandler open() throws SQLException {
		dbHelper = new DbHelper(context);
		sqlDatabase = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long insertScore(String userName, int playedCount, int wonCount,
			int lostCount, String against) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_USERNAME, userName);
		cv.put(KEY_PLAYED, playedCount);
		cv.put(KEY_WON, wonCount);
		cv.put(KEY_LOST, lostCount);
		cv.put(KEY_AGAINST, against);
		return sqlDatabase.insert(TABLE_SCORE, null, cv);
	}

	public SparseArray<String> getScores() {
		String[] columns = new String[] { KEY_USERID, KEY_USERNAME, KEY_PLAYED,
				KEY_WON, KEY_LOST, KEY_AGAINST };
		Cursor c = sqlDatabase.query(TABLE_SCORE, columns, null, null, null,
				null, null);
		int iId = c.getColumnIndex(KEY_USERID);
		int iName = c.getColumnIndex(KEY_USERNAME);
		int iPlayed = c.getColumnIndex(KEY_PLAYED);
		int iWon = c.getColumnIndex(KEY_WON);
		int iLost = c.getColumnIndex(KEY_LOST);
		int iAgainst = c.getColumnIndex(KEY_AGAINST);

		SparseArray<String> scores = new SparseArray<String>();

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			scores.put(
					c.getInt(iId),
					c.getString(iName) + " " + c.getInt(iPlayed) + " "
							+ c.getInt(iWon) + " " + c.getInt(iLost) + " "
							+ c.getString(iAgainst));
		}

		return scores;
	}

	@Deprecated
	public String convertScoresToString(SparseArray<String> scoreArray) {
		String scores = "";
		for (int i = scoreArray.size() - 1; i >= 0; --i) {
			scores += scoreArray.keyAt(i) + " " + scoreArray.valueAt(i) + "\n";
		}
		return scores;
	}

	public int getId(String userName) {
		String[] columns = new String[] { KEY_USERID };
		Cursor c = sqlDatabase.query(TABLE_SCORE, columns, KEY_USERNAME + "='"
				+ userName + "'", null, null, null, null);
		if (c == null) {
			return -1;
		}

		c.moveToFirst();
		if (c.isAfterLast()) {
			return 0;
		}
		return c.getInt(0);
	}

	public int updateScore(int userId, int playedCount, int wonCount,
			int lostCount, String against) {
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(KEY_PLAYED, playedCount);
		cvUpdate.put(KEY_WON, wonCount);
		cvUpdate.put(KEY_LOST, lostCount);
		cvUpdate.put(KEY_AGAINST, against);
		return sqlDatabase.update(TABLE_SCORE, cvUpdate, KEY_USERID + "="
				+ userId + " AND " + KEY_AGAINST + "='" + against + "'", null);
	}

	public int deleteScore(int userId) {
		return sqlDatabase.delete(TABLE_SCORE, KEY_USERID + "=" + userId, null);
	}

	public void deleteScores(int maxId) {
		for (int id = 1; id <= maxId; ++id) {
			deleteScore(id);
		}
	}

	public int updateOrSave(String userName, int playedCount, int wonCount,
			int lostCount, String against) {
		switch (updateScore(getId(userName), playedCount, wonCount, lostCount,
				against)) {
		case 1:
			return 1;
		case 0:
			return (int) insertScore(userName, playedCount, wonCount,
					lostCount, against);
		default:
			return -1;
		}
	}
}
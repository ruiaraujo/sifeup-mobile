package pt.up.fe.mobile.friends;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {
	// Tag to be used for debugging
	private static final String TAG = "DBAdapter";

	// fields in the friends table
	private static final String KEY_ID_FRIEND = "id_friends";
	private static final String KEY_CODE_FRIEND = "code_friends";
	private static final String KEY_NAME_FRIEND = "name_friend";
	private static final String KEY_COURSE_FRIEND = "course_friend";
	private static final String KEY_USER_CODE = "user_code";

	// database info
	private static final String DATABASE_NAME = "SiFEUPMobile";
	private static final String DATABASE_TABLE_FRIENDS = "friends";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_TABLE_FRIENDS = "CREATE TABLE "
			+ DATABASE_TABLE_FRIENDS + " (" + KEY_ID_FRIEND
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CODE_FRIEND
			+ " TEXT NOT NULL, " + KEY_NAME_FRIEND + " TEXT NOT NULL, "
			+ KEY_COURSE_FRIEND + " REAL NOT NULL, " + KEY_USER_CODE
			+ " TEXT NOT NULL  " + ");";

	private final Context context;

	private final DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	private static DBAdapter instance;

	/**
	 * @param ctx 
	 * @return instance of this class
	 */
	public static DBAdapter getInstance(final Context ctx) {
		if (instance == null)
			instance = new DBAdapter(ctx);
		return instance;
	}

	private DBAdapter(final Context ctx) {
		this.context = ctx.getApplicationContext();
		DBHelper = new DatabaseHelper(context);
		open();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_TABLE_FRIENDS);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FRIENDS + ";");
			onCreate(db);
		}

	}

	// ---opens the database---
	public synchronized boolean open() {
		try {
			if (db == null)
				db = DBHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ---closes the database---
	public synchronized void close() {
		DBHelper.close();
	}

	public List<Friend> getFriends(String code) {
		final List<Friend> friends = new ArrayList<Friend>();
		final Cursor mCursor = db.query(true, DATABASE_TABLE_FRIENDS,
				null,
				KEY_CODE_FRIEND + "='" + code + "'",
				null,
				null,
				null,
				KEY_NAME_FRIEND + " ASC",// ORDER BY
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			if (mCursor.getCount() == 0) {
				mCursor.close();
				return friends;
			}
			do {
				friends
						.add(new Friend(
								mCursor.getString(mCursor
										.getColumnIndex(KEY_CODE_FRIEND)),
								mCursor.getString(mCursor
										.getColumnIndex(KEY_NAME_FRIEND)),
								mCursor.getString(mCursor
										.getColumnIndex(KEY_COURSE_FRIEND))));
			} while (mCursor.moveToNext());
			mCursor.close();
		}
		return friends;
	}

	public boolean addFriend(Friend friend, String code) {
		final ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME_FRIEND, friend.getName());
		initialValues.put(KEY_CODE_FRIEND, friend.getCode());
		initialValues.put(KEY_COURSE_FRIEND, friend.getCourse());
		initialValues.put(KEY_USER_CODE, code);
		return db.insert(DATABASE_TABLE_FRIENDS, null, initialValues) != -1;
		
	}


	public boolean deleteFriend(Friend friend, String code) {
		return db.delete(DATABASE_TABLE_FRIENDS, KEY_CODE_FRIEND + "='"
				+ friend.getCode() + "' AND " + KEY_USER_CODE + "='" + code
				+ "'", null) > 0;
	}

}

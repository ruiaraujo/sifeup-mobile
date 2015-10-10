package pt.up.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotificationsTable {

	// fields in the profiles table
	static final String KEY_ID_USER = "_id";
	static final String KEY_ID_NOTIFCATION = "notification_id";
	static final String KEY_NOTIFICATION = "content";
	static final String KEY_STATE = "state";

	// database info
	static final String TABLE = "notifications";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE + " ("
			+ KEY_ID_USER + " TEXT NOT NULL , " + KEY_ID_NOTIFCATION
			+ " TEXT NOT NULL, " + KEY_NOTIFICATION + " TEXT NOT NULL, "
			+ KEY_STATE + " TEXT NOT NULL, PRIMARY KEY (" + KEY_ID_USER + ","
			+ KEY_ID_NOTIFCATION + "));";
	
	interface STATE {
		String NEW = "new";
		String SEEN = "seen";
	}

	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NotificationsTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

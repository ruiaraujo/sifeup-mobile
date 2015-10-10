package pt.up.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleTable implements BaseColumns {

	// fields in the profiles table
	static final String KEY_ID = "_id";
	static final String KEY_TYPE = "type";
	static final String KEY_INITIAL_DAY = "initial_day";
	static final String KEY_FINAL_DAY = "final_day";
	static final String KEY_CONTENT = "content";

	interface TYPE {
		String ROOM = "room";
		String STUDENT = "student";
		String EMPLOYEE = "employee";
		String CLASS = "class";
		String UC = "uc";
	}

	// database info
	static final String TABLE = "schedules";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE + " ("
			+ KEY_ID + " TEXT, " + KEY_CONTENT + " TEXT NOT NULL, " + KEY_TYPE
			+ " TEXT NOT NULL, " + KEY_INITIAL_DAY + " TEXT NOT NULL, "
			+ KEY_FINAL_DAY + " TEXT NOT NULL, " + SQL_CREATE_STATE
			+ ", PRIMARY KEY (" + KEY_ID + "," + KEY_INITIAL_DAY + ","
			+ KEY_TYPE + "));";

	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ScheduleTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

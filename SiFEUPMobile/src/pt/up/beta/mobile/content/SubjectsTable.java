package pt.up.beta.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class SubjectsTable implements BaseColumns {

	// Database table
	static final String TABLE = "subjects";

	static final String COLUMN_USER_NAME = "_id";
	static final String COLUMN_CODE = "code";
	static final String COLUMN_NAME_PT = "name_pt";
	static final String COLUMN_NAME_EN = "name_en";
	static final String COLUMN_CONTENT = "content";
	static final String COLUMN_FILES = "files";
	static final String COLUMN_COURSE_CODE = "course_id";
	static final String COLUMN_COURSE_NAME = "course_name";
	static final String COLUMN_ENTRY = "course_entry";

	// Database creation SQL statement
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE + "("
			+ COLUMN_USER_NAME + " TEXT NOT NULL, " + COLUMN_CODE
			+ " TEXT NOT NULL, " + COLUMN_NAME_PT + " TEXT ," + COLUMN_NAME_EN
			+ " TEXT DEFAULT NULL," + COLUMN_CONTENT + " TEXT NOT NULL,"
			+ COLUMN_FILES + " TEXT NOT NULL," + COLUMN_COURSE_CODE
			+ " TEXT DEFAULT NULL," + COLUMN_COURSE_NAME
			+ " TEXT DEFAULT NULL," + COLUMN_ENTRY + " TEXT DEFAULT NULL,"
			+ SQL_CREATE_STATE + ", PRIMARY KEY (" + COLUMN_USER_NAME + ","
			+ COLUMN_CODE + "));";

	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SubjectsTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

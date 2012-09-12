package pt.up.beta.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class SubjectsTable implements BaseColumns{

	// Database table
	static final String TABLE_SUBJECTS = "subjects";
	static final String COLUMN_USER_NAME = "_id";
	static final String COLUMN_CODE = "code";
	static final String COLUMN_NAME_PT = "name_pt";
	static final String COLUMN_NAME_EN = "name_en";
	static final String COLUMN_YEAR = "year";
	static final String COLUMN_PERIOD = "period";
	static final String COLUMN_CONTENT = "content";
	static final String COLUMN_FILES = "files";

	// Database creation SQL statement
	private static final String TABLE_CREATE = "create table "
			+ TABLE_SUBJECTS + "(" + COLUMN_USER_NAME + " text not null, "
			+ COLUMN_CODE + " text not null, " + COLUMN_YEAR
			+ " text not null," + COLUMN_PERIOD + " text not null,"
			+ COLUMN_NAME_PT + " text ," + COLUMN_NAME_EN + " text ,"
			+ COLUMN_CONTENT + " text not null," + COLUMN_FILES
			+ " text not null," + SQL_CREATE_STATE + ", PRIMARY KEY (" + COLUMN_USER_NAME + ","
			+ COLUMN_CODE + "," + COLUMN_YEAR + "," + COLUMN_PERIOD + "));";

	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SubjectsTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
		onCreate(database);
	}
}

package pt.up.beta.mobile.content.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubjectsTable {

	// Database table
	public static final String TABLE_SUBJECTS = "subjects";
	public static final String COLUMN_USER_CODE = "_id";
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_NAME_PT = "name_pt";
	public static final String COLUMN_NAME_EN = "name_en";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_PERIOD = "period";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_FILES = "files";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SUBJECTS + "(" + COLUMN_USER_CODE + " text not null, "
			+ COLUMN_CODE + " text not null, " + COLUMN_YEAR
			+ " text not null," + COLUMN_PERIOD + " text not null,"
			+ COLUMN_NAME_PT + " text not null," + COLUMN_NAME_EN + " text ,"
			+ COLUMN_CONTENT + " text not null," + COLUMN_FILES
			+ " text not null," + "PRIMARY KEY (" + COLUMN_USER_CODE + ","
			+ COLUMN_CODE + "," + COLUMN_YEAR + "," + COLUMN_PERIOD + "));";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SubjectsTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
		onCreate(database);
	}
}

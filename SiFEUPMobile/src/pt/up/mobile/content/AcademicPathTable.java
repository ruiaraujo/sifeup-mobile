package pt.up.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AcademicPathTable implements BaseColumns {

    // fields in the profiles table
    static final String KEY_ID_USER = "_id";
    static final String KEY_CONTENT = "content";


    // database info
    static final String TABLE = "academic_path";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE + " (" + KEY_ID_USER
            + " TEXT PRIMARY KEY , " + KEY_CONTENT
            + " TEXT DEFAULT NULL, " + SQL_CREATE_STATE
            + " );";
    
	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(AcademicPathTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

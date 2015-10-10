package pt.up.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsersTable {

    // fields in the profiles table
    static final String KEY_ID_PROFILE = "_id";
    static final String KEY_CODE = "code";
    static final String KEY_TYPE = "type";


    // database info
    static final String TABLE = "users";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE + " (" + KEY_ID_PROFILE
            + " TEXT PRIMARY KEY , " + KEY_CODE
            + " TEXT NOT NULL, " + KEY_TYPE
            + " TEXT NOT NULL);";
    
	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(UsersTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

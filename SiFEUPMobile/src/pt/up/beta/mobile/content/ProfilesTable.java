package pt.up.beta.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProfilesTable implements BaseColumns {

    // fields in the profiles table
    static final String KEY_ID_PROFILE = "_id";
    static final String KEY_CONTENT_PROFILE = "content";


    // database info
    static final String TABLE_PROFILES = "profiles";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_PROFILES + " (" + KEY_ID_PROFILE
            + " TEXT PRIMARY KEY , " + KEY_CONTENT_PROFILE
            + " TEXT NOT NULL, " + SQL_CREATE_STATE
            + " );";
    
	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ProfilesTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
		onCreate(database);
	}
}

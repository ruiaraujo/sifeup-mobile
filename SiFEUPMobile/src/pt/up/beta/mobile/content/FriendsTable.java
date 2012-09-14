package pt.up.beta.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FriendsTable  implements BaseColumns{

    // fields in the friends table
    static final String KEY_ID_FRIEND = "_id";
    static final String KEY_CODE_FRIEND = "code_friend";
    static final String KEY_NAME_FRIEND = "name_friend";
    static final String KEY_COURSE_FRIEND = "course_friend";
    static final String KEY_USER_CODE = "user_code";

    // database info
    static final String TABLE = "friends";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE + " (" + KEY_ID_FRIEND
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CODE_FRIEND
            + " TEXT NOT NULL, " + KEY_NAME_FRIEND + " TEXT NOT NULL, "
            + KEY_COURSE_FRIEND + " TEXT, " + KEY_USER_CODE
            + " TEXT NOT NULL );";
    
	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(FriendsTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

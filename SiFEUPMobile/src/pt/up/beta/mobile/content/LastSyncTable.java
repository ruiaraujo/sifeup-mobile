package pt.up.beta.mobile.content;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LastSyncTable {

    // fields in the profiles table
    static final String KEY_USER = "_id";
    static final String KEY_ACADEMIC_PATH = AcademicPathTable.TABLE;
    static final String KEY_CANTEENS = CanteensTable.TABLE;
    static final String KEY_EXAMS = ExamsTable.TABLE;
    static final String KEY_NOTIFICATIONS = NotificationsTable.TABLE;
    static final String KEY_PRINTING = PrintingQuotaTable.TABLE;
    static final String KEY_PROFILES = ProfilesTable.TABLE;
    static final String KEY_SCHEDULE = ScheduleTable.TABLE;
    static final String KEY_SUBJECTS = SubjectsTable.TABLE;
    static final String KEY_TUITION = TuitionTable.TABLE;
    static final String KEY_TEACHING_SERVICE = TeachingServiceTable.TABLE;


    // database info
    static final String TABLE = "last_sync";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE + " (" + KEY_USER
            + " TEXT PRIMARY KEY , " + KEY_ACADEMIC_PATH
            + " TEXT NOT NULL, " + KEY_CANTEENS
            + " TEXT NOT NULL, " + KEY_EXAMS
            + " TEXT NOT NULL, " + KEY_NOTIFICATIONS
            + " TEXT NOT NULL, " + KEY_PRINTING
            + " TEXT NOT NULL, " + KEY_PROFILES
            + " TEXT NOT NULL, " + KEY_SCHEDULE
            + " TEXT NOT NULL, " + KEY_SUBJECTS
            + " TEXT NOT NULL, " + KEY_TEACHING_SERVICE
            + " TEXT NOT NULL, " + KEY_TUITION
            + " TEXT NOT NULL);";
    
	static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LastSyncTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}

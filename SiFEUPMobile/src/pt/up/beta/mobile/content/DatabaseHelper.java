package pt.up.beta.mobile.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="sigarra.db";
	private static final int SCHEMA=2;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		SubjectsTable.onCreate(db);
		FriendsTable.onCreate(db);
		ProfilesTable.onCreate(db);
		ExamsTable.onCreate(db);
		AcademicPathTable.onCreate(db);
		TuitionTable.onCreate(db);
		PrintingQuotaTable.onCreate(db);
		ScheduleTable.onCreate(db);
		NotificationsTable.onCreate(db);
		CanteensTable.onCreate(db);
		LastSyncTable.onCreate(db);
		TeachingServiceTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SubjectsTable.onUpgrade(db, oldVersion, newVersion);
		FriendsTable.onUpgrade(db, oldVersion, newVersion);
		ProfilesTable.onUpgrade(db, oldVersion, newVersion);
		ExamsTable.onUpgrade(db, oldVersion, newVersion);
		AcademicPathTable.onUpgrade(db, oldVersion, newVersion);
		TuitionTable.onUpgrade(db, oldVersion, newVersion);
		PrintingQuotaTable.onUpgrade(db, oldVersion, newVersion);
		ScheduleTable.onUpgrade(db, oldVersion, newVersion);
		NotificationsTable.onUpgrade(db, oldVersion, newVersion);
		CanteensTable.onUpgrade(db, oldVersion, newVersion);
		LastSyncTable.onUpgrade(db, oldVersion, newVersion);
		TeachingServiceTable.onUpgrade(db, oldVersion, newVersion);
	}

}

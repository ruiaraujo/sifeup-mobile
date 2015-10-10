package pt.up.mobile.content;

import pt.up.mobile.Constants;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "sigarra.db";
	private static final int SCHEMA = 4;
	private final Context context;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
		this.context = context;
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
		UsersTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
		if (newVersion == 4) {
			UsersTable.onCreate(db);
			final AccountManager accountManager = AccountManager.get(context);
			Account[] accounts = accountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			for (Account account : accounts) {
				final ContentValues values = new ContentValues();
				values.put(UsersTable.KEY_CODE, accountManager.getUserData(
						account, "pt.up.mobile.USER_NAME"));
				values.put(UsersTable.KEY_TYPE, accountManager.getUserData(
						account, "pt.up.mobile.USER_TYPE"));
				values.put(UsersTable.KEY_ID_PROFILE, account.name);
				db.insert(UsersTable.TABLE, null, values);
			}
			// if version < 3 we need to upgrade evertything
			if (oldVersion == 3) {
				TeachingServiceTable.onUpgrade(db, oldVersion, newVersion);
				ScheduleTable.onUpgrade(db, oldVersion, newVersion);
			}
		}
		if (oldVersion >= 4)
			UsersTable.onUpgrade(db, oldVersion, newVersion);
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

package pt.up.beta.mobile.content;

import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SyncAdapterUtils;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SigarraProvider extends ContentProvider {

	// Used for the UriMacher
	private static final int LAST_SYNC = 0;
	private static final int SUBJECTS = 10;
	private static final int FRIENDS = 20;
	private static final int PROFILES = 30;
	private static final int PROFILES_PIC = 31;
	private static final int EXAMS = 40;
	private static final int ACADEMIC_PATH = 50;
	private static final int TUITION = 60;
	private static final int PRINTING_QUOTA = 70;
	private static final int SCHEDULE = 80;
	private static final int NOTIFICATIONS = 90;
	private static final int CANTEENS = 100;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_LAST_SYNC, LAST_SYNC);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SUBJECTS, SUBJECTS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_FRIENDS, FRIENDS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_PROFILES, PROFILES);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_PROFILES_PIC, PROFILES_PIC);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_EXAMS, EXAMS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_ACADEMIC_PATH, ACADEMIC_PATH);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_TUITION, TUITION);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_PRINTING, PRINTING_QUOTA);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SCHEDULE, SCHEDULE);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_NOTIFICATIONS, NOTIFICATIONS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_CANTEENS, CANTEENS);
	}

	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;

	private synchronized SQLiteDatabase getWritableDatabase() {
		if (database == null)
			database = dbHelper.getWritableDatabase();
		return database;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext().getApplicationContext());
		return dbHelper != null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		final String table;
		switch (uriType) {
		case LAST_SYNC:
			table = LastSyncTable.TABLE;
			break;
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			break;
		case PROFILES:
		case PROFILES_PIC:
			table = ProfilesTable.TABLE;
			break;
		case EXAMS:
			table = ExamsTable.TABLE;
			break;
		case ACADEMIC_PATH:
			table = AcademicPathTable.TABLE;
			break;
		case TUITION:
			table = TuitionTable.TABLE;
			break;
		case PRINTING_QUOTA:
			table = PrintingQuotaTable.TABLE;
			break;
		case SCHEDULE:
			table = ScheduleTable.TABLE;
			break;
		case NOTIFICATIONS:
			table = NotificationsTable.TABLE;
			break;
		case CANTEENS:
			table = CanteensTable.TABLE;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		final int count = getWritableDatabase().delete(table, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case LAST_SYNC:
			return SigarraContract.LastSync.CONTENT_TYPE;
		case SUBJECTS:
			return SigarraContract.Subjects.CONTENT_TYPE;
		case FRIENDS:
			return SigarraContract.Friends.CONTENT_TYPE;
		case PROFILES:
			return SigarraContract.Profiles.CONTENT_TYPE;
		case PROFILES_PIC:
			return SigarraContract.Profiles.CONTENT_PIC;
		case EXAMS:
			return SigarraContract.Exams.CONTENT_TYPE;
		case ACADEMIC_PATH:
			return SigarraContract.AcademicPath.CONTENT_TYPE;
		case TUITION:
			return SigarraContract.Tuition.CONTENT_TYPE;
		case PRINTING_QUOTA:
			return SigarraContract.PrintingQuota.CONTENT_TYPE;
		case SCHEDULE:
			return SigarraContract.Schedule.CONTENT_TYPE;
		case NOTIFICATIONS:
			return SigarraContract.Notifcations.CONTENT_TYPE;
		case CANTEENS:
			return SigarraContract.Canteens.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int uriType = sURIMatcher.match(uri);
		final String nullHack;
		final String table;
		switch (uriType) {
		case LAST_SYNC:
			table = LastSyncTable.TABLE;
			nullHack = null;
			break;
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			nullHack = null;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			nullHack = FriendsTable.KEY_COURSE_FRIEND;
			break;
		case PROFILES:
		case PROFILES_PIC:
			table = ProfilesTable.TABLE;
			nullHack = ProfilesTable.KEY_PROFILE_PIC;
			break;
		case EXAMS:
			table = ExamsTable.TABLE;
			nullHack = null;
			break;
		case ACADEMIC_PATH:
			table = AcademicPathTable.TABLE;
			nullHack = null;
			break;
		case TUITION:
			table = TuitionTable.TABLE;
			nullHack = null;
			break;
		case PRINTING_QUOTA:
			table = PrintingQuotaTable.TABLE;
			nullHack = null;
			break;
		case SCHEDULE:
			table = ScheduleTable.TABLE;
			nullHack = null;
			break;
		case NOTIFICATIONS:
			table = NotificationsTable.TABLE;
			nullHack = null;
			break;
		case CANTEENS:
			table = CanteensTable.TABLE;
			nullHack = null;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		final SQLiteDatabase db = getWritableDatabase();
		try {
			db.beginTransaction();
			for (ContentValues v : values) {
				if (db.replace(table, nullHack, v) == -1)
					throw new SQLException("Failed to insert row into " + uri);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

		if (uriType != LAST_SYNC && uriType != FRIENDS) {
			if (values.length > 0)
				updateLastSyncState(table);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return values.length;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		final String nullHack;
		final String table;
		switch (uriType) {
		case LAST_SYNC:
			table = LastSyncTable.TABLE;
			nullHack = null;
			break;
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			nullHack = null;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			nullHack = FriendsTable.KEY_COURSE_FRIEND;
			break;
		case PROFILES:
		case PROFILES_PIC:
			table = ProfilesTable.TABLE;
			nullHack = ProfilesTable.KEY_PROFILE_PIC;
			break;
		case EXAMS:
			table = ExamsTable.TABLE;
			nullHack = null;
			break;
		case ACADEMIC_PATH:
			table = AcademicPathTable.TABLE;
			nullHack = null;
			break;
		case TUITION:
			table = TuitionTable.TABLE;
			nullHack = null;
			break;
		case PRINTING_QUOTA:
			table = PrintingQuotaTable.TABLE;
			nullHack = null;
			break;
		case SCHEDULE:
			table = ScheduleTable.TABLE;
			nullHack = null;
			break;
		case NOTIFICATIONS:
			table = NotificationsTable.TABLE;
			nullHack = null;
			break;
		case CANTEENS:
			table = CanteensTable.TABLE;
			nullHack = null;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		long rowID = getWritableDatabase().replace(table, nullHack, values);
		if (rowID > 0) {
			if (uriType != LAST_SYNC && uriType != FRIENDS)
				updateLastSyncState(table);
			final Uri url = ContentUris.withAppendedId(uri, rowID);
			getContext().getContentResolver().notifyChange(url, null);
			getContext().getContentResolver().notifyChange(uri, null);
			return url;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		final String orderBy;
		final int uriType = sURIMatcher.match(uri);
		final Cursor c;
		switch (uriType) {
		case LAST_SYNC:
			qb.setTables(LastSyncTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				c.close();
				final ContentValues values = new ContentValues();
				values.put(SigarraContract.LastSync.ID, selectionArgs[0]);
				values.put(SigarraContract.LastSync.ACADEMIC_PATH, "0");
				values.put(SigarraContract.LastSync.CANTEENS, "0");
				values.put(SigarraContract.LastSync.EXAMS, "0");
				values.put(SigarraContract.LastSync.NOTIFICATIONS, "0");
				values.put(SigarraContract.LastSync.PRINTING, "0");
				values.put(SigarraContract.LastSync.PROFILES, "0");
				values.put(SigarraContract.LastSync.SCHEDULE, "0");
				values.put(SigarraContract.LastSync.SUBJECTS, "0");
				values.put(SigarraContract.LastSync.TUIION, "0");
				insert(uri, values);
				return query(uri, projection, selection, selectionArgs,
						sortOrder);
			}
			break;

		case SUBJECTS:
			qb.setTables(SubjectsTable.TABLE);
			if (TextUtils.isEmpty(sortOrder))
				orderBy = SigarraContract.Subjects.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, orderBy);
			if (c.getCount() == 0) {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState.getLong(syncState
							.getColumnIndex(SigarraContract.LastSync.SUBJECTS)) == 0
							|| selectionArgs.length == 3) {

						/*
						 * This means we re getting single subject
						 */
						if (selectionArgs.length == 3)
							SyncAdapterUtils.syncSubject(AccountUtils
									.getActiveUserName(getContext()),
									selectionArgs[0], selectionArgs[2],
									selectionArgs[1]);
						else
							SyncAdapterUtils.syncSubjects(AccountUtils
									.getActiveUserName(getContext()));

					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;
		case FRIENDS:
			qb.setTables(FriendsTable.TABLE);
			if (TextUtils.isEmpty(sortOrder))
				orderBy = SigarraContract.Friends.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, orderBy);
			break;
		case PROFILES:
			qb.setTables(ProfilesTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					new String[] { selectionArgs[0] }, null, null, sortOrder);
			if (c.getCount() == 0) {
				SyncAdapterUtils.syncProfile(
						AccountUtils.getActiveUserName(getContext()),
						selectionArgs[0], selectionArgs[1]);
			}
			break;
		case PROFILES_PIC:
			qb.setTables(ProfilesTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case EXAMS:
			qb.setTables(ExamsTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState.getLong(syncState
							.getColumnIndex(SigarraContract.LastSync.EXAMS)) == 0) {
						SyncAdapterUtils.syncExams(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;

		case ACADEMIC_PATH:
			qb.setTables(AcademicPathTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState
							.getLong(syncState
									.getColumnIndex(SigarraContract.LastSync.ACADEMIC_PATH)) == 0) {
						SyncAdapterUtils.syncAcademicPath(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;
		case TUITION:
			qb.setTables(TuitionTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState.getLong(syncState
							.getColumnIndex(SigarraContract.LastSync.TUIION)) == 0) {
						SyncAdapterUtils.syncTuitions(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;
		case PRINTING_QUOTA:
			qb.setTables(PrintingQuotaTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				SyncAdapterUtils.syncPrintingQuota(AccountUtils
						.getActiveUserName(getContext()));
			}
			break;
		case SCHEDULE:
			qb.setTables(ScheduleTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					new String[] { selectionArgs[0], selectionArgs[1],
							selectionArgs[2], selectionArgs[3] }, null, null,
					sortOrder);
			if (c.getCount() == 0) {
				SyncAdapterUtils.syncSchedule(
						AccountUtils.getActiveUserName(getContext()),
						selectionArgs[0], selectionArgs[1], selectionArgs[2],
						selectionArgs[3], selectionArgs[4]);

			}
			break;
		case NOTIFICATIONS:
			qb.setTables(NotificationsTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState
							.getLong(syncState
									.getColumnIndex(SigarraContract.LastSync.NOTIFICATIONS)) == 0) {
						SyncAdapterUtils.syncNotifications(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;

		case CANTEENS:
			qb.setTables(CanteensTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {

				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState.getLong(syncState
							.getColumnIndex(SigarraContract.LastSync.CANTEENS)) == 0) {
						SyncAdapterUtils.syncCanteens(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final int count;
		int uriType = sURIMatcher.match(uri);
		final String table;
		switch (uriType) {
		case LAST_SYNC:
			table = LastSyncTable.TABLE;
			break;
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			break;
		case PROFILES:
		case PROFILES_PIC:
			table = ProfilesTable.TABLE;
			break;
		case EXAMS:
			table = ExamsTable.TABLE;
			break;
		case ACADEMIC_PATH:
			table = AcademicPathTable.TABLE;
			break;
		case TUITION:
			table = TuitionTable.TABLE;
			break;
		case PRINTING_QUOTA:
			table = PrintingQuotaTable.TABLE;
			break;
		case SCHEDULE:
			table = ScheduleTable.TABLE;
			break;
		case NOTIFICATIONS:
			table = NotificationsTable.TABLE;
			break;
		case CANTEENS:
			table = CanteensTable.TABLE;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if (uriType != LAST_SYNC && uriType != FRIENDS)
			updateLastSyncState(table);
		count = getWritableDatabase().update(table, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	void updateLastSyncState(final String column) {
		final ContentValues values = new ContentValues();
		values.put(column, Long.toString(System.currentTimeMillis()));
		update(SigarraContract.LastSync.CONTENT_URI, values,
				SigarraContract.LastSync.PROFILE,
				SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
						.getActiveUserName(getContext())));
	}
}

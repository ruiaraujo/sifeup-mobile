package pt.up.mobile.content;

import pt.up.mobile.contacts.ContactManager;
import pt.up.mobile.contacts.ContactsSyncAdapterUtils;
import pt.up.mobile.sifeup.AccountUtils;
import pt.up.mobile.syncadapter.SigarraSyncAdapterUtils;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
	private static final int SUBJECT = 11;
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
	private static final int TEACHING_SERVICE = 110;
	private static final int USERS = 120;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_LAST_SYNC, LAST_SYNC);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SUBJECTS, SUBJECTS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SUBJECT, SUBJECT);
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
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_TEACHING_SERVICE, TEACHING_SERVICE);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_USERS, USERS);
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
		case SUBJECT:
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			// TODO: Add deleted flag to the friends table
			ContactManager.deleteContact(getContext(), selectionArgs[1]);
			break;
		case PROFILES:
		case PROFILES_PIC:
			table = ProfilesTable.TABLE;
			break;
		case EXAMS:
			table = ExamsTable.TABLE;
			break;
		case TEACHING_SERVICE:
			table = TeachingServiceTable.TABLE;
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
		case USERS:
			table = UsersTable.TABLE;
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
		case SUBJECT:
			return SigarraContract.Subjects.CONTENT_ITEM_TYPE;
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
		case TEACHING_SERVICE:
			return SigarraContract.TeachingService.CONTENT_TYPE;
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
		case USERS:
			return SigarraContract.Users.CONTENT_TYPE;
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
		case SUBJECT:
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			nullHack = null;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			nullHack = FriendsTable.KEY_TYPE_FRIEND;
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
		case TEACHING_SERVICE:
			table = TeachingServiceTable.TABLE;
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
		case USERS:
			table = UsersTable.TABLE;
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

		if (uriType != LAST_SYNC && uriType != FRIENDS && uriType != USERS) {
			if (values.length > 0)
				updateLastSyncState(getContext(), table);
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
		case SUBJECT:
		case SUBJECTS:
			table = SubjectsTable.TABLE;
			nullHack = null;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE;
			nullHack = FriendsTable.KEY_TYPE_FRIEND;
			ContactsSyncAdapterUtils.syncContacts(AccountUtils
					.getActiveUserName(getContext()));
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
		case TEACHING_SERVICE:
			table = TeachingServiceTable.TABLE;
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
		case USERS:
			table = UsersTable.TABLE;
			nullHack = null;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		long rowID = getWritableDatabase().replace(table, nullHack, values);
		if (rowID > 0) {
			if (uriType != LAST_SYNC && uriType != FRIENDS && uriType != USERS)
				updateLastSyncState(getContext(), table);
			final Uri url = ContentUris.withAppendedId(uri, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return url;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
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
				values.put(SigarraContract.LastSync.TEACHING_SERVICE, "0");
				insert(uri, values);
				return query(uri, projection, selection, selectionArgs,
						sortOrder);
			}
			break;
		case SUBJECT:
			qb.setTables(SubjectsTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				SigarraSyncAdapterUtils.syncSubject(
						AccountUtils.getActiveUserName(getContext()),
						selectionArgs[0]);
			}
			break;
		case SUBJECTS:
			qb.setTables(SubjectsTable.TABLE);
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
							.getColumnIndex(SigarraContract.LastSync.SUBJECTS)) == 0) {
						SigarraSyncAdapterUtils.syncSubjects(AccountUtils
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
				sortOrder = SigarraContract.Friends.DEFAULT_SORT;
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case USERS:
			qb.setTables(UsersTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case PROFILES:
			qb.setTables(ProfilesTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					new String[] { selectionArgs[0] }, null, null, sortOrder);
			if (c.getCount() == 0) {
				SigarraSyncAdapterUtils.syncProfile(
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
						SigarraSyncAdapterUtils.syncExams(AccountUtils
								.getActiveUserName(getContext()));
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
			break;
		case TEACHING_SERVICE:
			qb.setTables(TeachingServiceTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				SigarraSyncAdapterUtils.syncTeachingService(selectionArgs[0]);
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
						SigarraSyncAdapterUtils.syncAcademicPath(AccountUtils
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
						SigarraSyncAdapterUtils.syncTuitions(AccountUtils
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
				SigarraSyncAdapterUtils.syncPrintingQuota(AccountUtils
						.getActiveUserName(getContext()));
			}
			break;
		case SCHEDULE:
			qb.setTables(ScheduleTable.TABLE);
			c = qb.query(getWritableDatabase(), projection, selection,
					selectionArgs, null, null, sortOrder);
			if (c.getCount() == 0) {
				SigarraSyncAdapterUtils.syncSchedule(
						AccountUtils.getActiveUserName(getContext()),
						selectionArgs[0], selectionArgs[1], selectionArgs[2],
						selectionArgs[3]);

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
						SigarraSyncAdapterUtils.syncNotifications(AccountUtils
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
						SigarraSyncAdapterUtils.syncCanteens(AccountUtils
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
		case SUBJECT:
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
		case TEACHING_SERVICE:
			table = TeachingServiceTable.TABLE;
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
		case USERS:
			table = UsersTable.TABLE;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if (uriType != LAST_SYNC && uriType != FRIENDS && uriType != USERS)
			updateLastSyncState(getContext(), table);
		count = getWritableDatabase().update(table, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public static void updateLastSyncState(final Context context,
			final String column) {
		final ContentValues values = new ContentValues();
		values.put(column, Long.toString(System.currentTimeMillis()));
		context.getContentResolver().update(
				SigarraContract.LastSync.CONTENT_URI,
				values,
				SigarraContract.LastSync.PROFILE,
				SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
						.getActiveUserName(context)));
	}

	public static void deleteUserData(final Context context, final String user) {
		context.getContentResolver().delete(
				SigarraContract.Profiles.CONTENT_URI,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles.getProfilePicSelectionArgs(user));
		context.getContentResolver().delete(SigarraContract.Exams.CONTENT_URI,
				SigarraContract.Exams.PROFILE,
				SigarraContract.Exams.getExamsSelectionArgs(user));
		context.getContentResolver()
				.delete(SigarraContract.AcademicPath.CONTENT_URI,
						SigarraContract.AcademicPath.PROFILE,
						SigarraContract.AcademicPath
								.getAcademicPathSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.Schedule.CONTENT_URI,
				SigarraContract.Schedule.SCHEDULE_DELETE,
				SigarraContract.Schedule.getScheduleSelectionArgs(user));
		final Cursor c = context.getContentResolver()
				.query(SigarraContract.Friends.CONTENT_URI,
						SigarraContract.Friends.FRIENDS_COLUMNS,
						SigarraContract.Friends.USER_FRIENDS,
						SigarraContract.Friends
								.getUserFriendsSelectionArgs(user), null);
		try {
			if (c.moveToFirst()) {
				do {
					context.getContentResolver().delete(
							SigarraContract.Friends.CONTENT_URI,
							SigarraContract.Friends.FRIEND_SELECTION,
							SigarraContract.Friends.getFriendSelectionArgs(
									user, c.getString(0)));
				} while (c.moveToNext());
			}
		} finally {
			c.close();
		}
		context.getContentResolver().delete(
				SigarraContract.LastSync.CONTENT_URI,
				SigarraContract.LastSync.PROFILE,
				SigarraContract.LastSync.getLastSyncSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.Notifcations.CONTENT_URI,
				SigarraContract.Notifcations.PROFILE,
				SigarraContract.Notifcations
						.getNotificationsSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.PrintingQuota.CONTENT_URI,
				SigarraContract.PrintingQuota.PROFILE,
				SigarraContract.PrintingQuota
						.getPrintingQuotaSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.Subjects.CONTENT_URI,
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects.getUserSubjectsSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.TeachingService.CONTENT_URI,
				SigarraContract.TeachingService.PROFILE,
				SigarraContract.TeachingService
						.getTeachingServiceSelectionArgs(user));
		context.getContentResolver().delete(
				SigarraContract.Tuition.CONTENT_URI,
				SigarraContract.Tuition.PROFILE,
				SigarraContract.Tuition.getTuitionSelectionArgs(user));
		context.getContentResolver().delete(SigarraContract.Users.CONTENT_URI,
				SigarraContract.Users.PROFILE,
				SigarraContract.Users.getUserSelectionArgs(user));
	}

	public static void deleteCacheData(final Context context) {
		context.getContentResolver().delete(
				SigarraContract.Profiles.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.Schedule.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.AcademicPath.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.PrintingQuota.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.Subjects.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.TeachingService.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
		context.getContentResolver().delete(
				SigarraContract.Tuition.CONTENT_URI,
				BaseColumns.CACHE_SELECTION, SyncStates.CACHE_SELECTION);
	}
}
